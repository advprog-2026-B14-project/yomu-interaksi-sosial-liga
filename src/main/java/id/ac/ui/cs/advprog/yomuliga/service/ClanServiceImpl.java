package id.ac.ui.cs.advprog.yomuliga.service;

import id.ac.ui.cs.advprog.yomuliga.model.Clan;
import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import id.ac.ui.cs.advprog.yomuliga.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomuliga.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ClanServiceImpl implements ClanService {

    @Autowired
    private ClanRepository clanRepository;
    @Autowired
    private MemberRepository memberRepository;

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

        Clan clan = clanRepository.findById(clanId)
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

    @Override
    public double hitungSkorBerdasarkanTier(Clan clan, List<ClanMember> members) {
        if (members.isEmpty()) return 0.0;

        switch (clan.getTier().toUpperCase()) {
            case "BRONZE": // Penjumlahan total
                return members.stream().mapToDouble(ClanMember::getSkorIndividu).sum();

            case "SILVER": // Rata rata skor
                return members.stream().mapToDouble(ClanMember::getSkorIndividu).average().orElse(0.0);

            case "GOLD": // Skor top 5
                return members.stream().mapToDouble(ClanMember::getSkorIndividu)
                        .boxed().sorted(Collections.reverseOrder()).limit(5)
                        .mapToDouble(Double::doubleValue).sum();

            case "DIAMOND": // Rata rata tertimbang
                double tertimbang = 0, bobot = 0;
                for (ClanMember m : members) {
                    double b = m.getRole().equals("KETUA") ? 1.5 : 1.0;
                    tertimbang += m.getSkorIndividu() * b;
                    bobot += b;
                }
                return bobot > 0 ? (tertimbang / bobot) : 0.0;

            default:
                return 0.0;
        }
    }

    @Override
    public void refreshClanStats(UUID clanId) {
        Clan clan = clanRepository.findById(clanId).orElseThrow();
        List<ClanMember> members = memberRepository.findAllByClanId(clanId);

        double totalSkorBaru = hitungSkorBerdasarkanTier(clan, members);

        clan.setTotalSkor(totalSkorBaru);

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

    @Override
    public List<Clan> getAllLeaderboard() {
        return clanRepository.findAllByOrderByTotalSkorDesc();
    }

    @Override
    public List<Clan> getLeaderboardByTier(String tier) {
        return clanRepository.findAllByTierOrderByTotalSkorDesc(tier.toUpperCase());
    }
}