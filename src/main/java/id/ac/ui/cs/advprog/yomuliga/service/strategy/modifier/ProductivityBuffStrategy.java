package id.ac.ui.cs.advprog.yomuliga.service.strategy.modifier;


import id.ac.ui.cs.advprog.yomuliga.client.AchievementClient;
import org.springframework.stereotype.Component;

@Component
public class ProductivityBuffStrategy implements ScoreModifierStrategy {
    private final AchievementClient achievementClient;

    public ProductivityBuffStrategy(AchievementClient achievementClient) {
        this.achievementClient = achievementClient;
    }

    @Override
    public boolean isApplicable(String clanId) {
        // Cek apakah clan ini pantas dapet buff?
        double completionRate = achievementClient.getDailyMissionCompletionPercentage(clanId);
        return completionRate >= 0.5;
    }

    @Override
    public double applyModifier(double currentScore) {
        return currentScore * 1.2;
    }
}
