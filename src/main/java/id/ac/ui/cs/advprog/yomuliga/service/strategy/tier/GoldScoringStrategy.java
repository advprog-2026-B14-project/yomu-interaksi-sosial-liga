package id.ac.ui.cs.advprog.yomuliga.service.strategy.tier;

import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
public class GoldScoringStrategy implements TierScoringStrategy {
    @Override
    public double calculate(List<ClanMember> members) {
        return members.stream().mapToDouble(ClanMember::getSkorIndividu)
                .boxed().sorted(Collections.reverseOrder()).limit(5)
                .mapToDouble(Double::doubleValue).sum();
    }
    @Override
    public String getTierName() { return "GOLD"; }
}
