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
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import net.minecraftforge.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static net.minecraftforge.fml.common.ObfuscationReflectionHelper.getPrivateValue;
import static net.minecraftforge.fml.common.ObfuscationReflectionHelper.setPrivateValue;

/**
 * Handles reassignment of newly-spawned villagers.
 *
 * @author fresnobob43
 * @since 0.0.1
 */
class CareerAdvisor {

    // ================================================================================================
    // Cosntants

    // http://export.mcpbot.bspk.rs/stable/
    private static final String[] CAREERS_FIELD = {"careers"}; // i guess not obfuscated?
    private static final String[] CAREERLEVEL_FIELD = {"careerLevel", "field_175562_bw"};
    private static final String[] BUYINGLIST_FIELD = {"buyingList", "field_70963_i"};
    private static final String[] CAREERID_FIELD = {"careerId", "field_175563_bv"};

    // ================================================================================================
    // Fields

    private final Supplier<Collection<VillagerProfession>> professionSupplier;
    private final Function<String, Double> careerOdds;
    private final Logger logger;

    // ================================================================================================
    // Constructors

    CareerAdvisor(final Supplier<Collection<VillagerProfession>> professionsSupplier,
                  final Function<String, Double> careerOdds,
                  final Logger logger) {
        this.professionSupplier = requireNonNull(professionsSupplier);
        this.careerOdds = requireNonNull(careerOdds);
        this.logger = requireNonNull(logger);
    }

    // ================================================================================================
    // Public methods

    void selectCareerFor(EntityVillager villager) {
        final List<CareerOpportunity> ops;
        try {
            ops = getCareerOpportunities();
        } catch (ReflectiveOperationException e) {
            logger.error("unable to reassign villager career", e);
            return;
        }
        if (ops.size() == 0) {
            logger.debug("all career choices have zero odds, doing nothing");
        } else {
            final CareerOpportunity op = weightedRandomChoice(ops, () -> villager.getRNG().nextDouble());
            assignCareer(villager, op);
        }
    }

    // ================================================================================================
    // Private

    private List<CareerOpportunity> getCareerOpportunities() throws ReflectiveOperationException {
        final List<CareerOpportunity> out = new ArrayList<>();
        for (VillagerProfession p : this.professionSupplier.get()) {
            logger.trace("getting careers for profession " + p.getRegistryName());
            final List<VillagerCareer> careers;
            try {
                careers = getPrivateValue(VillagerProfession.class, p, CAREERS_FIELD);
            } catch (UnableToAccessFieldException uafe) {
                throw new ReflectiveOperationException(uafe);
            }
            for (VillagerCareer c : careers) {
                Double odds = this.careerOdds.apply(c.getName());
                if (odds == null) {
                    logger.debug("unknown career " + c.getName() + ", setting odds to 1.0");
                    odds = 1.0;
                } else if (odds > 0) {
                    out.add(new CareerOpportunity(p, c, odds));
                }
            }
        }
        return Collections.unmodifiableList(out);
    }

    CareerOpportunity weightedRandomChoice(List<CareerOpportunity> ops, Supplier<Double> dieRoller) {
        double range = 0;
        for (final CareerOpportunity op : ops) {
            range += op.getOdds();
        }
        double dieRoll = dieRoller.get() * range;
        for (final CareerOpportunity op : ops) {
            dieRoll -= op.getOdds();
            if (dieRoll <= 0) return op;
        }
        logger.error("Failed to make weighted selection. This is definitely a bug in TooManyClerics.  Defaulting to  " +
                ops.get(0) + " (" + dieRoll + " " + range + ")");
        return ops.get(0);
    }

    private void assignCareer(final EntityVillager villager, final CareerOpportunity c) {
        logger.debug("retraining " + villager.getDisplayName().getFormattedText() + " to be a " + c + " " + villager);
        final int villagerCareerLevel = 1;
        final int villagerCareerId;
        try {
            // EntityVillager.careerId is offset by one. form VillagerCareer ids.  because...reasons
            villagerCareerId = c.getCareerId() + 1;
        } catch (ReflectiveOperationException e) {
            logger.error("unable to assignCareer", e);
            return;
        }
        villager.setProfession(c.getProfession());
        try {
            // here we simulate what the vanilla code does with a new villager in populateBuyingList()
            logger.trace("updating " + CAREERLEVEL_FIELD[0]);
            setPrivateValue(EntityVillager.class, villager, villagerCareerLevel, CAREERLEVEL_FIELD);
            logger.trace("updating " + CAREERID_FIELD[0]);
            setPrivateValue(EntityVillager.class, villager, villagerCareerId, CAREERID_FIELD);
            logger.trace("updating " + BUYINGLIST_FIELD[0]);
            final MerchantRecipeList buyingList = new MerchantRecipeList();
            setPrivateValue(EntityVillager.class, villager, buyingList, BUYINGLIST_FIELD);
            {
                int i = villagerCareerId - 1;
                int j = villagerCareerLevel - 1;
                final List<EntityVillager.ITradeList> trades = villager.getProfessionForge().getCareer(i).getTrades(j);
                if (trades != null) {
                    final Iterator t = trades.iterator();
                    while (t.hasNext()) {
                        final ITradeList tradelist = (ITradeList) t.next();
                        tradelist.addMerchantRecipe(villager, buyingList, villager.getRNG());
                    }
                }
            }

        } catch (Exception e) {
            logger.error("unable to assignCareer, villager may be in a bad state!", e);
            return;
        }
        logger.debug("successfully updated fields");
    }

    static class CareerOpportunity {

        private final VillagerProfession profession;
        private final VillagerCareer career;
        private double odds;

        CareerOpportunity(final VillagerProfession profession, final VillagerCareer career, final double odds) {
            this.profession = requireNonNull(profession);
            this.career = requireNonNull(career);
            this.odds = odds;
        }

        double getOdds() {
            return odds;
        }

        public VillagerProfession getProfession() {
            return this.profession;
        }

        public int getCareerId() throws ReflectiveOperationException {
            try {
                return getPrivateValue(VillagerCareer.class, this.career, new String[]{"id"});
            } catch (Exception e) {
                throw new ReflectiveOperationException(e);
            }

        }

        @Override
        public String toString() {
            return career.getName();
        }
    }
}
