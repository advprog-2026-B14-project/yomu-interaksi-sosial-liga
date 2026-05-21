package id.ac.ui.cs.advprog.yomuliga.service.strategy.modifier;

import id.ac.ui.cs.advprog.yomuliga.client.AchievementClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductivityBuffStrategyTest {

    @Mock
    private AchievementClient achievementClient;

    @InjectMocks
    private ProductivityBuffStrategy strategy;

    private final String clanId = UUID.randomUUID().toString();

    @Test
    void testIsApplicable_TrueWhenCompletionRateAboveThreshold() {
        // Asumsi: misinya kelar 80% (0.8)
        when(achievementClient.getDailyMissionCompletionPercentage(clanId)).thenReturn(0.8);

        assertTrue(strategy.isApplicable(clanId));
    }

    @Test
    void testIsApplicable_TrueWhenCompletionRateExactlyAtThreshold() {
        // Asumsi: Misinya pas kelar 50% (0.5), masih dapet buff
        when(achievementClient.getDailyMissionCompletionPercentage(clanId)).thenReturn(0.5);

        assertTrue(strategy.isApplicable(clanId));
    }

    @Test
    void testIsApplicable_FalseWhenCompletionRateBelowThreshold() {
        // Asumsi: misinya cuma kelar 30% (0.3), gagal dapet buff
        when(achievementClient.getDailyMissionCompletionPercentage(clanId)).thenReturn(0.3);

        assertFalse(strategy.isApplicable(clanId));
    }

    @Test
    void testApplyModifier() {
        double currentScore = 100.0;
        double expectedScore = 120.0; // 100 * 1.2

        assertEquals(expectedScore, strategy.applyModifier(currentScore));
    }
}