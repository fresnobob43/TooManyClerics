package net.fresnobob.tmc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * @author fresnobob
 * @since 0.0.1
 */
class AmnesiaPotion extends Potion {

    private static final boolean IS_BAD_EFFECT = true;
    private static final int LIQUID_COLOR = -12345;

    private final CareerAdvisor careerAdvisor;
    private final Logger logger;

    AmnesiaPotion(final CareerAdvisor careerAdvisor, final Logger logger) {
        super(IS_BAD_EFFECT, LIQUID_COLOR);
        this.careerAdvisor = requireNonNull(careerAdvisor);
        this.logger = requireNonNull(logger);
    }

    void register(IForgeRegistry<PotionType> registry) {
        this.setRegistryName("Potion of Amnesia");
        this.setPotionName("Potion of Amnesia");
        PotionEffect pe = new PotionEffect(this);
        PotionType pt = new PotionType(pe);
        pt.setRegistryName("Potion of Amnesia Type");
        registry.registerAll(pt);
        {
            ItemStack input = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), registry.getValue(new ResourceLocation("awkward")));
            ItemStack ingredient = new ItemStack(Items.EMERALD);
            ItemStack output = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), pt);
            BrewingRecipeRegistry.addRecipe(new BrewingRecipe(input, ingredient, output));
        }
    }

    @Override
    public void affectEntity(@Nullable Entity entity, @Nullable Entity entity2, EntityLivingBase entity3, int effect, double effect2) {
        logger.debug("=============================== affect entity on  " + entity + " entity2: " + entity2 + " entity3:  " + entity3 +
                " effect: " + effect);
        if (entity3 instanceof EntityVillager) {
            careerAdvisor.selectCareerFor((EntityVillager) entity3);

        }
    }

    @Override
    public boolean isInstant() {
        return true;
    }

}
