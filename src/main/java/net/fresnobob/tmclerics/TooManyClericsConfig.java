package net.fresnobob.tmclerics;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

/**
 * Config using the forge framework.
 *
 * @author fresnobob43
 * @since 0.0.1
 */
@Config(modid = TooManyClericsMod.MODID, name = TooManyClericsMod.NAME)
public class TooManyClericsConfig {

    @Comment("Whether newborn villager career choices should be rebalanced.  Set to false for vanilla behavior.  Change requires restart.")
    public static boolean enableNewbornCareerBalancing = true;

    @Comment("Whether Potions of Amnesia can be brewed.  Change requires restart.")
    public static boolean enableAmnesiaPotions = true;

}
