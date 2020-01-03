package net.fresnobob.tmclerics;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import java.lang.ref.WeakReference;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * Fixes career assignment for cured zomies.  The basic idea is to listen for entity adds and removes
 * for cases where a Villager is added at exactly the same time and place as a ZombieVillager is removed.
 * When that happens, it's pretty much guaranteed to be a zombie getting cured.
 *
 * @author fresnobob43
 * @since 0.0.2
 */
@Immutable
class CuredZombieHandler {

    // ================================================================================================
    // Fields

    private final CareerAdvisor careerAdvisor;
    private final Logger logger;

    // ================================================================================================
    // Constructors

    CuredZombieHandler(final CareerAdvisor careerAdvisor, final Logger logger) {
        this.careerAdvisor = requireNonNull(careerAdvisor);
        this.logger = requireNonNull(logger);
    }

    // ================================================================================================
    // Event handlers

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        event.getWorld().addEventListener(new CuredZombieWorldListener(careerAdvisor, logger));
    }

    // FIXME? it's unclear whether i'm responsible for doing this.  it sort of looks like not
    //@SubscribeEvent
    //public void onWorldUnload(WorldEvent.Unload event) {}

    // ================================================================================================
    // Inner classes


    /**
     * Records the time and location of some event on an entity.  This helps us determine whether two entities are
     * really just the two sides of a zombie conversion.
     */
    @Immutable
    private static class EventTimeAndPlace<T extends Entity> {
        private final long eventTime;
        private final double[] eventLocation;
        private final WeakReference<T> entity;
        private final Logger logger;

        private EventTimeAndPlace(final T entity, final Logger logger) {
            this.eventTime = entity.getEntityWorld().getTotalWorldTime();
            this.eventLocation = new double[]{entity.posX, entity.posY, entity.posZ};
            this.entity = new WeakReference(requireNonNull(entity));
            this.logger = requireNonNull(logger);
        }

        boolean isIncidentWith(final EventTimeAndPlace that) {
            if (that == this) {
                logger.error("ignoring isIncidentWith(self) " + this);
                return false;
            }
            if (that == null) {
                logger.error("ignoring isInidentWith(null) " + this);
                return false;
            }
            logger.trace("comparing " + this + " with " + that);
            return this.eventTime == that.eventTime && Arrays.equals(this.eventLocation, that.eventLocation);
        }

        T getEntity() {
            return this.entity.get();
        }

        public String toString() {
            return this.eventTime + " " + eventLocation[0] + " " + eventLocation[1] + " " + eventLocation[2] + " " + getEntity();
        }
    }


    private static class CuredZombieWorldListener implements IWorldEventListener {

        private final CareerAdvisor careerAdvisor;
        private EventTimeAndPlace<EntityZombieVillager> lastZombieRemoval;
        private EventTimeAndPlace<EntityVillager> lastVillagerAddition;
        private final Logger logger;

        CuredZombieWorldListener(CareerAdvisor careerAdvisor, Logger logger) {
            this.careerAdvisor = requireNonNull(careerAdvisor);
            this.logger = requireNonNull(logger);
        }

        @Override
        public void onEntityAdded(Entity entity) {
            if (entity.getServer() == null) {
                logger.error("why is CuredZombieListener.onEntityAdded being called on the client?");
                return;
            }
            if (entity instanceof EntityVillager) {
                final EntityVillager villager = (EntityVillager) entity;
                try {
                    logger.debug("villager added " + villager.prevPosX + " " + villager.posX + " " + villager.posY + " " + villager.posZ + " " + villager.getWorld().getTotalWorldTime());
                    final EventTimeAndPlace<EntityVillager> etp = new EventTimeAndPlace(villager, logger);
                    if (lastZombieRemoval != null && lastZombieRemoval.isIncidentWith(etp)) {
                        logger.debug("added villager seems to be a cured zombie, adjusting career choice.  " + villager);
                        careerAdvisor.selectCareerFor(villager);
                        this.lastZombieRemoval = null;
                        this.lastVillagerAddition = null;
                    } else {
                        this.lastZombieRemoval = null;
                        this.lastVillagerAddition = etp;
                    }
                } catch (Exception e) {
                    logger.error("failed to process removed Villager " + entity, e);
                }
            }
        }

        @Override
        public void onEntityRemoved(Entity entity) {
            if (entity.getServer() == null) {
                logger.error("why is CuredZombieListener.onEntityRemoved being called on the client?");
                return;
            }
            if (entity instanceof EntityZombieVillager) {
                final EntityZombieVillager zv = (EntityZombieVillager) entity;
                try {
                    logger.debug("ZombieVillager removed " + zv.prevPosX + " " + zv.posX + " " + zv.posY + " " + zv.posZ +
                            " " + zv.getEntityWorld().getTotalWorldTime());
                    final EventTimeAndPlace<EntityZombieVillager> etp = new EventTimeAndPlace(zv, logger);
                    if (lastVillagerAddition != null && lastVillagerAddition.isIncidentWith(etp)) {
                        final EntityVillager villager = this.lastVillagerAddition.getEntity();
                        if (villager == null) {
                            logger.warn("lastVillager added has been gc'ed?");
                        } else {
                            logger.debug("removed ZombieVillager seems to have been cured, adjusting villager's career choice " + villager);
                            careerAdvisor.selectCareerFor(villager);
                        }
                        this.lastZombieRemoval = null;
                        this.lastVillagerAddition = null;
                    } else {
                        lastVillagerAddition = null;
                        lastZombieRemoval = etp;
                    }
                } catch (Exception e) {
                    logger.error("failed to process removed ZombieVillager " + entity, e);
                }
            }
        }

        // we don't care about any other notifications...
        
        @Override
        public void notifyBlockUpdate(World world, BlockPos blockPos, IBlockState iBlockState, IBlockState iBlockState1, int i) {
        }

        @Override
        public void notifyLightSet(BlockPos blockPos) {
        }

        @Override
        public void markBlockRangeForRenderUpdate(int i, int i1, int i2, int i3, int i4, int i5) {
        }

        @Override
        public void playSoundToAllNearExcept(@Nullable EntityPlayer entityPlayer, SoundEvent soundEvent, SoundCategory soundCategory, double v, double v1, double v2, float v3, float v4) {
        }

        @Override
        public void playRecord(SoundEvent soundEvent, BlockPos blockPos) {
        }

        @Override
        public void spawnParticle(int i, boolean b, double v, double v1, double v2, double v3, double v4, double v5, int... ints) {
        }

        @Override
        public void spawnParticle(int i, boolean b, boolean b1, double v, double v1, double v2, double v3, double v4, double v5, int... ints) {
        }


        @Override
        public void broadcastSound(int i, BlockPos blockPos, int i1) {
        }

        @Override
        public void playEvent(EntityPlayer entityPlayer, int i, BlockPos blockPos, int i1) {
        }

        @Override
        public void sendBlockBreakProgress(int i, BlockPos blockPos, int i1) {
        }
    }
}
