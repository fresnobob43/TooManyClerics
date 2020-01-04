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

    @Comment("Whether newborn villager career choices should be rebalanced.  Set to false for vanilla behavior.")
    @Config.RequiresWorldRestart
    public static boolean enableNewbornCareerBalancing = true;

    @Comment("Whether cured zombie career choices should be rebalanced.  Set to false for vanilla behavior.")
    @Config.RequiresWorldRestart
    public static boolean enableCuredZombieCareerBalancing = true;

    @Comment("Whether Potions of Amnesia can be brewed.")
    @Config.RequiresMcRestart
    public static boolean enableAmnesiaPotions = true;

    @Comment("Adjusts the relative frequency for specific careers.  0 = 'never spawn'")
    public static CareerFrequencyConfig careerFrequencies = new CareerFrequencyConfig();

    public static class CareerFrequencyConfig {

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
                    return careerFrequencies.Armorer;
                case "butcher":
                    return careerFrequencies.Butcher;
                case "cartographer":
                    return careerFrequencies.Cartographer;
                case "cleric":
                    return careerFrequencies.Cleric;
                case "farmer":
                    return careerFrequencies.Farmer;
                case "fletcher":
                    return careerFrequencies.Fletcher;
                case "fisherman":
                    return careerFrequencies.Fisherman;
                case "leather":
                    return careerFrequencies.Leatherworker;
                case "librarian":
                    return careerFrequencies.Librarian;
                case "nitwit":
                    return careerFrequencies.Nitwit;
                case "shepherd":
                    return careerFrequencies.Shepherd;
                case "tool":
                    return careerFrequencies.Toolsmith;
                case "weapon":
                    return careerFrequencies.Weaponsmith;
                default:
                    return null;
            }
        }
    }
}
