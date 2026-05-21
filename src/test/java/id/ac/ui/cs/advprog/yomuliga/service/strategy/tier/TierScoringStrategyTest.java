package id.ac.ui.cs.advprog.yomuliga.service.strategy.tier;

import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TierScoringStrategyTest {

    private BronzeScoringStrategy bronzeStrategy;
    private SilverScoringStrategy silverStrategy;
    private GoldScoringStrategy goldStrategy;
    private DiamondScoringStrategy diamondStrategy;

    @BeforeEach
    void setUp() {
        bronzeStrategy = new BronzeScoringStrategy();
        silverStrategy = new SilverScoringStrategy();
        goldStrategy = new GoldScoringStrategy();
        diamondStrategy = new DiamondScoringStrategy();
    }

    @Test
    void testGetTierName() {
        assertEquals("BRONZE", bronzeStrategy.getTierName());
        assertEquals("SILVER", silverStrategy.getTierName());
        assertEquals("GOLD", goldStrategy.getTierName());
        assertEquals("DIAMOND", diamondStrategy.getTierName());
    }

    @Test
    void testCalculate() {
        List<ClanMember> dummyMembers = new ArrayList<>();
        ClanMember member1 = new ClanMember();
        member1.setSkorIndividu(100.0);
        dummyMembers.add(member1);

        assertDoesNotThrow(() -> {
            double bronzeScore = bronzeStrategy.calculate(dummyMembers);
            double silverScore = silverStrategy.calculate(dummyMembers);
            double goldScore = goldStrategy.calculate(dummyMembers);
            double diamondScore = diamondStrategy.calculate(dummyMembers);

            assertFalse(Double.isNaN(bronzeScore));
            assertFalse(Double.isNaN(silverScore));
            assertFalse(Double.isNaN(goldScore));
            assertFalse(Double.isNaN(diamondScore));
        });
    }
}