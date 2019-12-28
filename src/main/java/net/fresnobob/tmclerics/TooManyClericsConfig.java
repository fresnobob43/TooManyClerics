package net.fresnobob.tmclerics;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

/**
 * @author fresnobob43
 * @since 0.0.1
 */
@Config(modid = TooManyClericsMod.MODID, name = TooManyClericsMod.NAME)
public class TooManyClericsConfig {

    public static CareerBalancing rebalancing = new CareerBalancing();
    public static AmnesiaPotion amnesiaPotion = new AmnesiaPotion();

    public static class CareerBalancing {

        @Comment("Whether newborn villager career choices should be rebalanced.  Set to false for vanilla behavior.")
        public boolean enabled = true;
    }

    public static class AmnesiaPotion {

        @Comment("Whether Potions of Amnesia are available.")
        public boolean enabled = true;
    }
}
