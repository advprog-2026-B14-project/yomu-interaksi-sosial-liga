package id.ac.ui.cs.advprog.yomuliga.service;

import id.ac.ui.cs.advprog.yomuliga.model.Clan;
import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface ClanService {
    Clan createClan(String name, UUID leaderId);
    void joinClan(UUID clanId, UUID userId);
    double hitungSkorBerdasarkanTier(Clan clan, List<ClanMember> members);
    void refreshClanStats(UUID clanId);

    @Transactional
    void addScoreToMember(UUID clanId, UUID userId, double pointsGained);

    @Transactional
    void endOfSeason();

    List<Clan> getAllLeaderboard();
    List<Clan> getLeaderboardByTier(String tier);
}