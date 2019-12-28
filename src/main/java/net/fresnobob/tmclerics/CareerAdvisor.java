package net.fresnobob.tmclerics;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Supplier;


import static java.util.Objects.requireNonNull;
import static net.fresnobob.tmclerics.ReflectionUtils.getFieldValue;
import static net.fresnobob.tmclerics.ReflectionUtils.setFieldValue;

class CareerAdvisor {

    // ================================================================================================
    // Fields

    private final Supplier<Collection<VillagerProfession>> professionSupplier;
    private final Logger logger;

    // ================================================================================================
    // Constructors

    CareerAdvisor(Supplier<Collection<VillagerProfession>> professionSupplier, Logger logger) {
        this.professionSupplier = requireNonNull(professionSupplier);
        this.logger = requireNonNull(logger);
    }

    // ================================================================================================
    // Public methods

    void selectCareerFor(EntityVillager villager) {
        final List<CareerOpportunity> ops = getCareerOpportunities();
        final CareerOpportunity op = weightedRandomChoice(ops, () -> villager.getRNG().nextDouble());
        assignCareer(villager, op);
    }

    // ================================================================================================
    // Private

    private List<CareerOpportunity> getCareerOpportunities() {
        final List<CareerOpportunity> out = new ArrayList<>();
        for (VillagerProfession p : this.professionSupplier.get()) {
            final List<VillagerCareer> careers = getFieldValue("careers", VillagerProfession.class, p, List.class);
            for (VillagerCareer c : careers) {
                out.add(new CareerOpportunity(p, c));
            }
        }
        return Collections.unmodifiableList(out);
    }

    private CareerOpportunity weightedRandomChoice(List<CareerOpportunity> ops, Supplier<Double> dieRoller) {
        double range = 0;
        for (final CareerOpportunity op : ops) {
            range += op.getPreferenece();
        }
        double dieRoll = dieRoller.get() * range;
        for (final CareerOpportunity op : ops) {
            dieRoll -= op.getPreferenece();
            if (dieRoll <= 0) return op;
        }
        logger.error("Failed to make weighted selection. This is definitely a bug.");
        return ops.get(0);
    }

    private void assignCareer(final EntityVillager villager, final CareerOpportunity c) {
        logger.info("Retraining " + villager.getDisplayName().getFormattedText() + "  to be a " + c + " " + villager);
        villager.setProfession(c.getProfession());
        setFieldValue("careerId", EntityVillager.class, villager, c.getCareerId());
        setFieldValue("careerLevel", EntityVillager.class, villager, 1);
        setFieldValue("buyingList", EntityVillager.class, villager, null);
    }

    private static class CareerOpportunity {

        private final VillagerProfession profession;
        private final VillagerCareer career;
        private double preference = 1.0;

        CareerOpportunity(final VillagerProfession profession, final VillagerCareer career) {
            this.profession = requireNonNull(profession);
            this.career = requireNonNull(career);
        }

        double getPreferenece() {
            return preference;
        }

        public VillagerProfession getProfession() {
            return this.profession;
        }

        public int getCareerId() {
            return getFieldValue("id", VillagerCareer.class, career, Integer.class);
        }

        @Override
        public String toString() {
            return career.getName() + getCareerId();
        }


    }
}
