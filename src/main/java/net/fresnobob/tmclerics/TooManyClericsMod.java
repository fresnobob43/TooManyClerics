//
// The MIT License (MIT)
//
// Copyright (c) 2019 fresnobob43
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject to
// the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
// OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
// WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
//
package net.fresnobob.tmclerics;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.CuredZombieVillagerTrigger;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.CreativeSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;
import static net.minecraftforge.fml.common.ObfuscationReflectionHelper.getPrivateValue;

/**
 * Main mod class.
 *
 * @author fresnobob43
 * @since 0.0.1
 */
@Mod(modid = TooManyClericsMod.MODID, name = TooManyClericsMod.NAME, version = TooManyClericsMod.VERSION)
@Mod.EventBusSubscriber(modid = TooManyClericsMod.MODID)
public class TooManyClericsMod {

    // ================================================================================================
    // Constants

    static final String VERSION = "1.12.2-0.0.2";
    static final String MODID = "tmclerics";
    static final String NAME = "Too Many Clerics";

    // ================================================================================================
    // Mod lifecycle handlers

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        final Logger logger = event.getModLog();
        logger.info("initializing");
        final CareerAdvisor careerAdvisor = new CareerAdvisor(() -> ForgeRegistries.VILLAGER_PROFESSIONS.getValuesCollection(), logger);
        if (TooManyClericsConfig.enableNewbornCareerBalancing) {
            logger.debug("enabling rebalancing");
            MinecraftForge.EVENT_BUS.register(new BabyHandler(careerAdvisor, logger));
        }
        if (TooManyClericsConfig.enableAmnesiaPotions) {
            logger.debug("enabling amnesiaPotion");
            MinecraftForge.EVENT_BUS.register(new PotionHandler(new AmnesiaPotion(careerAdvisor, logger), logger));
        }
        MinecraftForge.EVENT_BUS.register(new WorldLoadHandler(careerAdvisor, logger));
        // MinecraftForge.EVENT_BUS.register(new EntityJoinWorldHandler(careerAdvisor, logger));

    }

    // ================================================================================================
    // Game event handlers

    private static class EntityJoinWorldHandler {

        private final Logger logger;

        private final CareerAdvisor careerAdvisor;

        EntityJoinWorldHandler(CareerAdvisor careerAdvisor, Logger logger) {
            this.careerAdvisor = requireNonNull(careerAdvisor);
            this.logger = requireNonNull(logger);
        }

        @SubscribeEvent
        public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
            logger.debug("onPlayerLoggedIn event");
            if (event.player.isServerWorld()) {
                EntityPlayerMP player = (EntityPlayerMP) event.player;
                PlayerAdvancements advancements = player.getAdvancements();
                CuredZombieVillagerTrigger.Instance criterionInstance = new CuredZombieVillagerTrigger.Instance(null, null) {

                    public boolean test(EntityPlayerMP p_test_1_, EntityZombie p_test_2_, EntityVillager p_test_3_) {
                        return true;
                    }
                };
                Advancement advancement = null;
                final String criterionName = null;
                ICriterionTrigger.Listener<CuredZombieVillagerTrigger.Instance> l =
                        new ICriterionTrigger.Listener(criterionInstance, advancement, criterionName) {

                            private Object hashCode = new Object();

                            @Override
                            public void grantCriterion(PlayerAdvancements grant) {
                                logger.debug("granted zombie cure advancement " + grant);
                            }

                            @Override
                            public boolean equals(Object o) {
                                return o == this;
                            }

                            @Override
                            public int hashCode() {
                                return hashCode.hashCode();
                            }
                        };

                CriteriaTriggers.CURED_ZOMBIE_VILLAGER.addListener(advancements, l);
                logger.debug("added listener for zombie cure for " + player);
            }

        }


        @SubscribeEvent
        public void onEntitySpawn(LivingSpawnEvent.CheckSpawn event) {
            if (event.getEntity() instanceof EntityVillager) {
                final EntityVillager villager = (EntityVillager) event.getEntity();
                try {
                    int careerId = getPrivateValue(EntityVillager.class, villager, new String[]{"careerId"});
                    logger.debug("onEntitySpawn: villager added with careerId = " + careerId + "   " + villager);
                } catch (Exception e) {
                    logger.error("failed to lookup careerId", e);
                }
            }

        }

        @SubscribeEvent
        public void onEntityJoin(EntityJoinWorldEvent event) {
            if (event.getEntity() instanceof EntityVillager) {
                final EntityVillager villager = (EntityVillager) event.getEntity();
                PotionEffect nausuea = villager.getActivePotionEffect(MobEffects.NAUSEA);
                //logger.debug("villager nausea = " + nausuea + " world time = " + event.getWorld().getWorldTime() + " " + event.getWorld().getTotalWorldTime()) + " " + Minecraft.getW.;
                if (nausuea != null && nausuea.getDuration() == 200 && nausuea.getAmplifier() == 0) {
                    logger.debug("I think this villager has been cured zzz " + villager);
                }
                try {
                    int careerId = getPrivateValue(EntityVillager.class, villager, new String[]{"careerId"});
                    logger.debug("!!!entityJoin: villager added with careerId = " + careerId + "   " + villager);
                } catch (Exception e) {
                    logger.error("failed to lookup careerId", e);
                }

                StackTraceElement[] trace = Thread.currentThread().getStackTrace();
                for (StackTraceElement t : trace) {
                    logger.debug("%% " + t.getClassName() + " " + t.getMethodName());
                }
            }
        }
    }

    private static class WorldLoadHandler {

        private final Logger logger;
        private final CareerAdvisor careerAdvisor;


        WorldLoadHandler(CareerAdvisor careerAdvisor, Logger logger) {
            this.careerAdvisor = requireNonNull(careerAdvisor);
            this.logger = requireNonNull(logger);
        }

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event) {
            event.getWorld().addEventListener(new CuredZombieHandler(event.getWorld(), careerAdvisor, logger));
        }
    }


    private static class CuredZombieHandler implements IWorldEventListener {

        private final Logger logger;
        private final CareerAdvisor careerAdvisor;
        private final World world;
        private Long lastZombieVillagerRemovedTime;
        private Long lastVillagerAddedTime;
        private EntityVillager lastVillagerAdded;

        CuredZombieHandler(World world, CareerAdvisor careerAdvisor, Logger logger) {
            this.world = requireNonNull(world);
            this.careerAdvisor = requireNonNull(careerAdvisor);
            this.logger = requireNonNull(logger);
        }

        @Override
        public void onEntityAdded(Entity entity) {
            if (entity.getServer() == null) return;
            if (entity instanceof EntityVillager) {
                final EntityVillager villager = (EntityVillager) entity;
                try {
                    logger.debug("world time = " + this.world.getTotalWorldTime() + "serverTicks = " + entity.getServer().getTickCounter());
                    logger.debug("villager added " + villager.prevPosX + " " + villager.posX + " " + villager.posY + " " + villager.posZ + " " + villager.getWorld().getTotalWorldTime());
                    if (lastZombieVillagerRemovedTime != null) {
                        if (lastZombieVillagerRemovedTime == villager.getEntityWorld().getTotalWorldTime()) {
                            logger.debug("got it!!!");
                            careerAdvisor.selectCareerFor(villager);
                        }
                        lastZombieVillagerRemovedTime = null;
                        lastVillagerAddedTime = null;
                        lastVillagerAdded = null;
                    } else {
                        this.lastVillagerAddedTime = villager.getEntityWorld().getTotalWorldTime();
                        logger.debug("set lastVillagerAdded time to " + this.lastVillagerAddedTime);
                        this.lastVillagerAdded = villager;
                    }
                } catch (Exception e) {
                    logger.error("failed to lookup careerId", e);
                }
            }
            reset(entity);
        }


        private void reset(Entity entity) {
            if (lastVillagerAdded != null) {
                if (lastVillagerAddedTime != null && lastVillagerAddedTime < entity.getEntityWorld().getTotalWorldTime()) {
                    lastVillagerAdded = null;
                    lastVillagerAddedTime = null;
                    logger.debug("reset villagerAdded");
                }
            }

        }

        @Override
        public void onEntityRemoved(Entity entity) {
            if (entity.getServer() == null) return;
            if (entity instanceof EntityZombieVillager) {
                final EntityZombieVillager z = (EntityZombieVillager) entity;
                try {
                    logger.debug("world time = " + this.world.getTotalWorldTime() + "serverTicks = " + entity.getServer().getTickCounter());
                    logger.debug("zombie removed " + z.prevPosX + " " + z.posX + " " + z.posY + " " + z.posZ + " " + z.getEntityWorld().getTotalWorldTime());
                    if (lastVillagerAddedTime != null && lastVillagerAdded != null) {
                        if (lastVillagerAddedTime.equals(z.getEntityWorld().getTotalWorldTime())) {
                            logger.debug("got it!!!");
                            careerAdvisor.selectCareerFor(lastVillagerAdded);
                        }
                        lastZombieVillagerRemovedTime = null;
                        lastVillagerAddedTime = null;
                        lastVillagerAdded = null;
                    } else {
                        lastZombieVillagerRemovedTime = z.getEntityWorld().getTotalWorldTime();
                        logger.debug("set lastZombieVillagerRemovedTime time to " + this.lastZombieVillagerRemovedTime);
                    }
                } catch (Exception e) {
                    logger.error("failed to lookup careerId", e);
                }
            }
            reset(entity);
        }

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

    private static class BabyHandler {

        private final Logger logger;
        private final CareerAdvisor careerAdvisor;

        BabyHandler(CareerAdvisor careerAdvisor, Logger logger) {
            this.careerAdvisor = requireNonNull(careerAdvisor);
            this.logger = requireNonNull(logger);
        }


        @SubscribeEvent
        public void onBabySpawn(BabyEntitySpawnEvent event) {
            // https://github.com/MinecraftForge/MinecraftForge/issues/1687
            logger.debug("handling baby spawn event " + event);
            if (event.getChild() instanceof EntityVillager) {
                careerAdvisor.selectCareerFor(((EntityVillager) event.getChild()));
            }

        }
    }


    private static class PotionHandler {

        private final Logger logger;
        private final AmnesiaPotion amnesiaPotion;

        PotionHandler(AmnesiaPotion amnesiaPotion, Logger logger) {
            this.amnesiaPotion = requireNonNull(amnesiaPotion);
            this.logger = requireNonNull(logger);
        }

        @SubscribeEvent
        public void registerPotions(RegistryEvent.Register<Potion> event) {
            logger.debug("registering AmnesiaPotion ");
            event.getRegistry().register(amnesiaPotion);
        }

        @SubscribeEvent
        public void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
            logger.debug("registering AmnesiaPotion type");
            amnesiaPotion.register(event.getRegistry());
        }
    }


}
