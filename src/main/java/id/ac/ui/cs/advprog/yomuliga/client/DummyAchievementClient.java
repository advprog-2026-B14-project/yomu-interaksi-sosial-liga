package id.ac.ui.cs.advprog.yomuliga.client;

import org.springframework.stereotype.Service;

@Service
public class DummyAchievementClient implements AchievementClient {

    @Override
    public double getDailyMissionCompletionPercentage(String clanId) {

        if ("CLAN-PRO-01".equals(clanId)) {
            return 0.8;
        } else if ("CLAN-NOOB-02".equals(clanId)) {
            return 0.2;
        }
        return 0.5;
    }
}