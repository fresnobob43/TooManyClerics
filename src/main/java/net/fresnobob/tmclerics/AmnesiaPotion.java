package net.fresnobob.tmclerics;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;
import static net.fresnobob.tmclerics.TooManyClericsMod.MODID;

/**
 * @author fresnobob43
 * @since 0.0.1
 */
class AmnesiaPotion extends Potion {

    // ================================================================================================
    // Constants

    private static final String AWKWARD = "awkward";
    private static final String NAME = "amnesia";
    private static final boolean IS_BAD_EFFECT = true;
    private static final int LIQUID_COLOR = 43520;

    // ================================================================================================
    // Fields

    private final CareerAdvisor careerAdvisor;
    private final Logger logger;

    // ================================================================================================
    // Constructor

    AmnesiaPotion(final CareerAdvisor careerAdvisor, final Logger logger) {
        super(IS_BAD_EFFECT, LIQUID_COLOR);
        this.careerAdvisor = requireNonNull(careerAdvisor);
        this.logger = requireNonNull(logger);
        this.setRegistryName(MODID, NAME + ".effect");
    }

    void register(IForgeRegistry<PotionType> registry) {
        logger.debug("registering AmnesiaPotion type");
        requireNonNull(registry);
        PotionEffect pe = new PotionEffect(this);
        PotionType pt = new PotionType(MODID + "." + NAME, pe);
        registry.register(pt.setRegistryName(MODID, NAME));
        {
            final ResourceLocation r = new ResourceLocation(AWKWARD);
            final PotionType awkwardPotion = registry.getValue(new ResourceLocation(AWKWARD));
            if (awkwardPotion == null) {
                logger.error("failed to load " + r + ", Potion of Amnesia will not be brewable!");
            } else {
                requireNonNull(awkwardPotion, "couldn't not load " + AWKWARD + " potion");
                ItemStack input = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), awkwardPotion);
                ItemStack ingredient = new ItemStack(Items.EMERALD);
                ItemStack output = PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), pt);
                BrewingRecipeRegistry.addRecipe(new CustomBrewingRecipe(input, ingredient, output));
            }
        }

    }

    // ================================================================================================
    // Potion implementation

    @Override
    public void affectEntity(@Nullable Entity entity, @Nullable Entity entity2, EntityLivingBase entity3, int effect, double effect2) {
        logger.debug("affectEntity " + entity + " entity2: " + entity2 + " entity3:  " + entity3 + " effect: " + effect);
        if (entity3 instanceof EntityVillager) {
            careerAdvisor.selectCareerFor((EntityVillager) entity3);
        }
    }

    @Override
    public boolean isInstant() {
        return true;
    }


    /**
     * Because isInput() in the base class is broken?  I don't fully understand.
     * https://www.minecraftforge.net/forum/topic/60632-two-questions-when-creating-custom-potions/
     */

    class CustomBrewingRecipe extends BrewingRecipe {

        public CustomBrewingRecipe(@Nonnull ItemStack input, @Nonnull ItemStack ingredient, @Nonnull ItemStack output) {
            super(input, ingredient, output);
        }

        @Override
        public boolean isInput(@Nonnull ItemStack stack) {
            return ItemStack.areItemStacksEqualUsingNBTShareTag(stack, getInput());
        }
    }

}



