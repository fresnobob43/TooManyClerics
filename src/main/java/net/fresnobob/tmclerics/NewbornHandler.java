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


import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.concurrent.Immutable;

import static java.util.Objects.requireNonNull;

/**
 * Fixes career assignment for newborn villagers.
 *
 * @author fresnobob43
 * @since 0.0.1
 */
@Immutable
class NewbornHandler {

    private final Logger logger;
    private final CareerAdvisor careerAdvisor;

    NewbornHandler(CareerAdvisor careerAdvisor, Logger logger) {
        this.careerAdvisor = requireNonNull(careerAdvisor);
        this.logger = requireNonNull(logger);
    }

    @SubscribeEvent
    public void onBabySpawn(BabyEntitySpawnEvent event) {
        if (event.getChild().getServer() == null) {
            logger.debug("ignoring non-server BabySpawnEvent");
            return;
        }
        // https://github.com/MinecraftForge/MinecraftForge/issues/1687
        logger.debug("handling BabyEntitySpawnEvent " + event);
        if (event.getChild() instanceof EntityVillager) {
            careerAdvisor.selectCareerFor(((EntityVillager) event.getChild()));
        }

    }
}
