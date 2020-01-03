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

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;

import java.util.function.Function;

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

    @Comment("Whether cured zombie career choices should be rebalanced.  Set to false for vanilla behavior.  Change requires restart.")
    public static boolean enableCuredZombieCareerBalancing = true;

    @Comment("Whether Potions of Amnesia can be brewed.  Change requires restart.")
    public static boolean enableAmnesiaPotions = true;

    @Comment("Adjusts the relative frequency for specific careers.  0 = 'never spawn'")
    public static OddsPerCareer oddsPerCareer = new OddsPerCareer();

    public static class OddsPerCareer {

        private static double DEFAULT_ODDS = 1.0;

        @Config.RangeDouble(min = 0)
        public double Armorer = DEFAULT_ODDS;

        @Config.RangeDouble(min = 0)
        public double Butcher = DEFAULT_ODDS;

        @Config.RangeDouble(min = 0)
        public double Cartographer = DEFAULT_ODDS;

        @Config.RangeDouble(min = 0)
        public double Cleric = DEFAULT_ODDS;

        @Config.RangeDouble(min = 0)
        public double Farmer = DEFAULT_ODDS;

        @Config.RangeDouble(min = 0)
        public double Fletcher = DEFAULT_ODDS;

        @Config.RangeDouble(min = 0)
        public double Fisherman = DEFAULT_ODDS;

        @Config.RangeDouble(min = 0)
        public double Leatherworker = DEFAULT_ODDS;

        @Config.RangeDouble(min = 0)
        public double Librarian = DEFAULT_ODDS;

        @Config.RangeDouble(min = 0)
        public double Nitwit = DEFAULT_ODDS;

        @Config.RangeDouble(min = 0)
        public double Shepherd = DEFAULT_ODDS;

        @Config.RangeDouble(min = 0)
        public double Toolsmith = DEFAULT_ODDS;

        @Config.RangeDouble(min = 0)
        public double Weaponsmith = DEFAULT_ODDS;

    }

    static class ConfiguredCareerOdds implements Function<String, Double> {

        @Override
        public Double apply(String careerName) {
            switch (careerName) {
                case "armor":
                    return oddsPerCareer.Armorer;
                case "butcher":
                    return oddsPerCareer.Butcher;
                case "cartographer":
                    return oddsPerCareer.Cartographer;
                case "cleric":
                    return oddsPerCareer.Cleric;
                case "farmer":
                    return oddsPerCareer.Farmer;
                case "fletcher":
                    return oddsPerCareer.Fletcher;
                case "fisherman":
                    return oddsPerCareer.Fisherman;
                case "leather":
                    return oddsPerCareer.Leatherworker;
                case "librarian":
                    return oddsPerCareer.Librarian;
                case "nitwit":
                    return oddsPerCareer.Nitwit;
                case "shepherd":
                    return oddsPerCareer.Shepherd;
                case "tool":
                    return oddsPerCareer.Toolsmith;
                case "weapon":
                    return oddsPerCareer.Weaponsmith;
                default:
                    return null;
            }
        }
    }
}
