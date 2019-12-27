package net.fresnobob.tmc;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Logger;

import static java.util.Objects.requireNonNull;

@Mod(modid = TooManyClericsMod.MODID, name = TooManyClericsMod.NAME, version = TooManyClericsMod.VERSION)
@Mod.EventBusSubscriber(modid = TooManyClericsMod.MODID)
public class TooManyClericsMod {

    // ================================================================================================
    // Constants

    static final String MODID = "TooManyClerics";
    static final String NAME = "Too Many Clerics";
    static final String VERSION = "12.2-0.0.1";

    // ================================================================================================
    // Fields

    private Logger logger;
    private CareerAdvisor careerAdvisor;
    private AmnesiaPotion amnesiaPotion;

    // ================================================================================================
    // Mod lifecycle handlers

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        logger.info("initializing");
        MinecraftForge.EVENT_BUS.register(this);
        careerAdvisor = new CareerAdvisor(() -> ForgeRegistries.VILLAGER_PROFESSIONS.getValuesCollection(), logger);
        this.amnesiaPotion = new AmnesiaPotion(careerAdvisor, logger);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    // ================================================================================================
    // Game event handlers

    @SubscribeEvent
    public void onBabySpawn(BabyEntitySpawnEvent event) {
        // https://github.com/MinecraftForge/MinecraftForge/issues/1687
        logger.debug("baby spawn event " + event);
        if (event.getChild() instanceof EntityVillager) {
            this.careerAdvisor.selectCareerFor(((EntityVillager) event.getChild()));
        }

    }

    @SubscribeEvent
    public void registerPotionTypes(RegistryEvent.Register<PotionType> event) {
        if (this.amnesiaPotion == null) throw new IllegalStateException("amnesiaPotion not inited");
        amnesiaPotion.register(event.getRegistry());

    }


}
