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
