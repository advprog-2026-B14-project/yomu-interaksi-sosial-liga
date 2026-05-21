package id.ac.ui.cs.advprog.yomuliga.service;

import id.ac.ui.cs.advprog.yomuliga.model.Clan;
import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import id.ac.ui.cs.advprog.yomuliga.model.ClanTier;
import id.ac.ui.cs.advprog.yomuliga.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomuliga.repository.MemberRepository;
import id.ac.ui.cs.advprog.yomuliga.service.strategy.modifier.ScoreModifierStrategy;
import id.ac.ui.cs.advprog.yomuliga.service.strategy.tier.TierScoringStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClanServiceImpl implements ClanService {


    private final ClanRepository clanRepository;
    private final MemberRepository memberRepository;
    private final Map<ClanTier, TierScoringStrategy> tierStrategies;
    private final List<ScoreModifierStrategy> modifierStrategies;

    public ClanServiceImpl(ClanRepository clanRepository,
                           MemberRepository memberRepository,
                           List<TierScoringStrategy> tierStrategiesList,
                           List<ScoreModifierStrategy> modifierStrategies) {
        this.clanRepository = clanRepository;
        this.memberRepository = memberRepository;
        this.modifierStrategies = modifierStrategies;

        this.tierStrategies = tierStrategiesList.stream()
                .collect(Collectors.toMap(
                        strategy -> ClanTier.fromString(strategy.getTierName()),
                        strategy -> strategy
                ));
    }

    @Override
    public Clan createClan(String name, UUID leaderId) {
        if (clanRepository.findByNamaClan(name).isPresent()) {
            throw new RuntimeException("Nama Clan sudah ada!");
        }

        Clan clan = new Clan();
        clan.setNamaClan(name);
        clan.setIdKetua(leaderId);
        clan.updateTier();
        return clanRepository.save(clan);
    }

    @Override
    public void joinClan(UUID clanId, UUID userId) {

        clanRepository.findById(clanId)
                .orElseThrow(() -> new RuntimeException("Clan tidak ditemukan!"));

        if (memberRepository.findByUserId(userId).isPresent()) {
            throw new RuntimeException("Kamu sudah bergabung dengan Clan lain!");
        }

        ClanMember member = new ClanMember();
        member.setClanId(clanId);
        member.setUserId(userId);
        member.setRole("ANGGOTA");

        member.setSkorIndividu(0.0);

        memberRepository.save(member);

        refreshClanStats(clanId);
    }

    // Strategy Pattern
    @Override
    public double hitungSkorBerdasarkanTier(Clan clan, List<ClanMember> members) {
        if (members.isEmpty()) return 0.0;

        ClanTier currentTier = ClanTier.fromString(clan.getTier());
        TierScoringStrategy strategy = tierStrategies.get(currentTier);

        if (strategy == null) {
            return 0.0;
        }

        return strategy.calculate(members);
    }

    @Override
    public void refreshClanStats(UUID clanId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();
        List<ClanMember> members = memberRepository.findAllByClanId(clanId);

        double baseScore = hitungSkorBerdasarkanTier(clan, members);
        double finalScore = applyBuffAndDebuff(clanId.toString(), baseScore);

        clan.setTotalSkor(finalScore);

        clan.updateTier();

        clanRepository.save(clan);
    }

    @Transactional
    @Override
    public void addScoreToMember(UUID clanId, UUID userId, double pointsGained) {
        ClanMember member = memberRepository.findByClanIdAndUserId(clanId, userId)
                .orElseThrow(() -> new RuntimeException("Member tidak ditemukan"));

        member.setSkorIndividu(member.getSkorIndividu() + pointsGained);
        memberRepository.save(member);

       refreshClanStats(clanId);
    }

    @Transactional
    @Override
    public void endOfSeason() {
        List<Clan> clans = clanRepository.findAll();
        if (clans.isEmpty()) {
            return;
        }

        Map<ClanTier, List<Clan>> clansByTier = clans.stream()
                .collect(Collectors.groupingBy(clan -> ClanTier.fromString(clan.getTier())));

        Map<UUID, String> updatedTiers = new HashMap<>();

        for (ClanTier currentTier : ClanTier.values()) {
            List<Clan> tierClans = new ArrayList<>(clansByTier.getOrDefault(currentTier, List.of()));

            tierClans.sort(Comparator.comparingDouble(this::getClanTotalScore).reversed());

            int moveCount = calculateSeasonMoveCount(tierClans.size());
            if (moveCount == 0) {
                continue;
            }

            ClanTier promotedTier = currentTier.next();
            ClanTier relegatedTier = currentTier.previous();

            for (int i = 0; i < moveCount; i++) {
                Clan promotedClan = tierClans.get(i);
                if (promotedTier != null) {
                    updatedTiers.put(promotedClan.getId(), promotedTier.name());
                }
            }

            for (int i = tierClans.size() - moveCount; i < tierClans.size(); i++) {
                Clan relegatedClan = tierClans.get(i);
                if (relegatedTier != null) {
                    updatedTiers.put(relegatedClan.getId(), relegatedTier.name());
                }
            }
        }

        // Update tier clan dan reset skor
        for (Clan clan : clans) {
            String safeTierString = ClanTier.fromString(clan.getTier()).name();

            clan.setTier(updatedTiers.getOrDefault(clan.getId(), safeTierString));
            clan.setTotalSkor(0.0);
        }

        clanRepository.saveAll(clans);
    }

    @Override
    public List<Clan> getAllLeaderboard() {
        return clanRepository.findAllByOrderByTotalSkorDesc();
    }

    @Override
    public List<Clan> getLeaderboardByTier(String tier) {
        return clanRepository.findAllByTierOrderByTotalSkorDesc(tier.toUpperCase());
    }


    private double applyBuffAndDebuff(String clanId, double baseScore) {
        double finalScore = baseScore;

        for (ScoreModifierStrategy modifier : modifierStrategies) {
            if (modifier.isApplicable(clanId)) {
                finalScore = modifier.applyModifier(finalScore);
            }
        }
        return finalScore;
    }

    private int calculateSeasonMoveCount(int clanCount) {
        if (clanCount <= 1) {
            return 0;
        }

        return Math.min((int) Math.ceil(clanCount * 0.2), clanCount / 2);
    }

    private double getClanTotalScore(Clan clan) {
        return clan.getTotalSkor() == null ? 0.0 : clan.getTotalSkor();
    }
}