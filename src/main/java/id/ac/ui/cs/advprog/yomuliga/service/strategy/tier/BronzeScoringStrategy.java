package id.ac.ui.cs.advprog.yomuliga.service.strategy.tier;

import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class BronzeScoringStrategy implements TierScoringStrategy {
    @Override
    public double calculate(List<ClanMember> members) {
        return members.stream().mapToDouble(ClanMember::getSkorIndividu).sum();
    }
    @Override
    public String getTierName() { return "BRONZE"; }
}
