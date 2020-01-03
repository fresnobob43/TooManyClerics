package net.fresnobob.tmclerics;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.concurrent.Immutable;

import static java.util.Objects.requireNonNull;

/**
 * Fixes career assignment for newborn villagers.
 *
 * @author fresnobob43
 * @since 0.0.1
 */
@Immutable
class NewbornHandler {

    private final Logger logger;
    private final CareerAdvisor careerAdvisor;

    NewbornHandler(CareerAdvisor careerAdvisor, Logger logger) {
        this.careerAdvisor = requireNonNull(careerAdvisor);
        this.logger = requireNonNull(logger);
    }

    @SubscribeEvent
    public void onBabySpawn(BabyEntitySpawnEvent event) {
        // https://github.com/MinecraftForge/MinecraftForge/issues/1687
        logger.debug("handling BabyEntitySpawnEvent " + event);
        if (event.getChild() instanceof EntityVillager) {
            careerAdvisor.selectCareerFor(((EntityVillager) event.getChild()));
        }

    }
}
