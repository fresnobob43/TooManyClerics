package net.fresnobob.tmclerics;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;

import static net.fresnobob.tmclerics.CareerAdvisor.CareerOpportunity;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author fresnobob43
 * @since 0.0.1
 */
public class CareerAdvisorTest {

    private final Logger logger = LogManager.getLogger();
    private final VillagerProfession BROWN_ROBE = new VillagerProfession("brown", "brown", "brown");
    private final VillagerProfession PURPLE_ROBE = new VillagerProfession("purple", "purple", "purple");
    private final VillagerCareer FARMER = new VillagerCareer(BROWN_ROBE, "farmer");
    private final VillagerCareer FLETCHER = new VillagerCareer(BROWN_ROBE, "fletcher");
    private final VillagerCareer CLERIC = new VillagerCareer(PURPLE_ROBE, "cleric");

    @Test
    public void testWeightedRandomChoice() {
        final List<VillagerProfession> profs = ImmutableList.of(BROWN_ROBE, PURPLE_ROBE);
        final CareerAdvisor ca = new CareerAdvisor(() -> profs, s -> 1.0, logger);
        final CareerOpportunity farmerOp = new CareerOpportunity(BROWN_ROBE, FARMER, 1.0);
        final CareerOpportunity fletcherOp = new CareerOpportunity(BROWN_ROBE, FLETCHER, 1.0);
        final CareerOpportunity clericOp = new CareerOpportunity(BROWN_ROBE, CLERIC, 1.0);
        final List<CareerOpportunity> ops = ImmutableList.of(farmerOp, fletcherOp, clericOp);
        assertTrue(farmerOp == ca.weightedRandomChoice(ops, () -> 0.0));
        assertTrue(farmerOp == ca.weightedRandomChoice(ops, () -> 0.333));
        assertTrue(fletcherOp == ca.weightedRandomChoice(ops, () -> 0.666));
        assertTrue(clericOp == ca.weightedRandomChoice(ops, () -> 0.900));
        assertTrue(farmerOp == ca.weightedRandomChoice(ops, () -> 9999.0)); // edge case
        assertTrue(farmerOp == ca.weightedRandomChoice(ops, () -> -9999.0)); // edge case
    }

}
