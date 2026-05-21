package id.ac.ui.cs.advprog.yomuliga.service.strategy.tier;

import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import java.util.List;

public interface TierScoringStrategy {
    double calculate(List<ClanMember> members);
    String getTierName();
}
