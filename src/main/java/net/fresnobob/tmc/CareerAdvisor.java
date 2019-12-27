package net.fresnobob.tmc;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;


import static java.util.Objects.requireNonNull;
import static net.fresnobob.tmc.ReflectionUtils.getFieldValue;
import static net.fresnobob.tmc.ReflectionUtils.setFieldValue;

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
        final CareerOpportunity op = ops.get(villager.getRNG().nextInt(ops.size()));
        assignCareer(villager, op);
    }

    // ================================================================================================
    // Private

    private void assignCareer(final EntityVillager villager, final CareerOpportunity c) {
        logger.info("Retraining villager " + villager + "  to be a " + c);
        villager.setProfession(c.getProfession());
        setFieldValue("careerId", EntityVillager.class, villager, c.getCareerId());
        setFieldValue("careerLevel", EntityVillager.class, villager, 1);
        setFieldValue("buyingList", EntityVillager.class, villager, null);
    }

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

    private static class CareerOpportunity {

        private final VillagerProfession profession;
        private final VillagerCareer career;
        private double preference = 1.0;

        CareerOpportunity(VillagerProfession profession, VillagerCareer career) {
            this.profession = requireNonNull(profession);
            this.career = requireNonNull(career);
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
