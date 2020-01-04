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

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
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

    static final String VERSION = "1.12.2-0.0.2";
    static final String MODID = "tmclerics";
    static final String NAME = "TooManyClerics";

    // ================================================================================================
    // Mod lifecycle handlers

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        final Logger logger = requireNonNull(event.getModLog());
        logger.info("preInit " + event.getSide());
        final CareerAdvisor careerAdvisor = new CareerAdvisor(() -> ForgeRegistries.VILLAGER_PROFESSIONS.getValuesCollection(),
                new TooManyClericsConfig.ConfiguredCareerOdds(), logger);
        MinecraftForge.EVENT_BUS.register(new ConfigChangeHandler(logger));
        if (TooManyClericsConfig.enableNewbornCareerBalancing) {
            logger.debug("enabling newborn balancing");
            MinecraftForge.EVENT_BUS.register(new NewbornHandler(careerAdvisor, logger));
        }
        if (TooManyClericsConfig.enableCuredZombieCareerBalancing) {// && Side.SERVER == event.getSide()) {
            logger.debug("enabling cured zombie balancing");
            MinecraftForge.EVENT_BUS.register(new CuredZombieHandler(careerAdvisor, logger));
        }
        if (TooManyClericsConfig.enableAmnesiaPotions) {
            logger.debug("enabling amnesiaPotion");
            MinecraftForge.EVENT_BUS.register(new AmnesiaPotionHandler(careerAdvisor, logger));
        }
    }

    /**
     * Handle saving config changes from the GUI.
     */
    private static class ConfigChangeHandler {

        private final Logger logger;

        ConfigChangeHandler(Logger logger) {
            this.logger = requireNonNull(logger);
        }

        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         */
        @SubscribeEvent
        public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(TooManyClericsMod.MODID)) {
                logger.debug("config changed, syncing");
                ConfigManager.sync(MODID, Config.Type.INSTANCE);
            }
        }
    }
}
