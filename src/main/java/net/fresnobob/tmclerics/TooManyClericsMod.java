package net.fresnobob.tmclerics;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Logger;

import static java.util.Objects.requireNonNull;

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

    static final String VERSION = "1.12.2-0.0.1";
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
    }

    // ================================================================================================
    // Game event handlers

    private class BabyHandler {

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
