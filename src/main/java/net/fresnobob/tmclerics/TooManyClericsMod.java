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

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;

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

    static final String VERSION = "1.12.2-0.0.1";
    static final String MODID = "tmclerics";
    static final String NAME = "Too Many Clerics";

    private Logger logger;

    // ================================================================================================
    // Mod lifecycle handlers

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        this.logger = event.getModLog();
        logger.info("initializing " + event.getSide());
        final CareerAdvisor careerAdvisor = new CareerAdvisor(() -> ForgeRegistries.VILLAGER_PROFESSIONS.getValuesCollection(), logger);
        if (TooManyClericsConfig.enableNewbornCareerBalancing && Side.SERVER == event.getSide()) {
            logger.debug("enabling newborn balancing");
            MinecraftForge.EVENT_BUS.register(new NewbornHandler(careerAdvisor, logger));
        }
        if (TooManyClericsConfig.enableCuredZombieCareerBalancing) {// && Side.SERVER == event.getSide()) {
            logger.debug("enabling cured zombie balancing");
            MinecraftForge.EVENT_BUS.register(new CuredZombieHandler(careerAdvisor, logger));
        }
        if (TooManyClericsConfig.enableAmnesiaPotions) {
            logger.debug("enabling amnesiaPotion");
            MinecraftForge.EVENT_BUS.register(new PotionHandler(new AmnesiaPotion(careerAdvisor, logger), logger));
        }
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event) {
        logger.info("onServerStart!!!!!!!!!!!!");
    }
    // ================================================================================================
    // Game event handlers

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
