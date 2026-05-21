package id.ac.ui.cs.advprog.yomuliga.service.strategy.tier;

import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DiamondScoringStrategy implements TierScoringStrategy {
    @Override
    public double calculate(List<ClanMember> members) {
        double tertimbang = 0, bobot = 0;
        for (ClanMember m : members) {
            double b = m.getRole().equals("KETUA") ? 1.5 : 1.0;
            tertimbang += m.getSkorIndividu() * b;
            bobot += b;
        }
        return bobot > 0 ? (tertimbang / bobot) : 0.0;
    }
    @Override
    public String getTierName() { return "DIAMOND"; }
}
