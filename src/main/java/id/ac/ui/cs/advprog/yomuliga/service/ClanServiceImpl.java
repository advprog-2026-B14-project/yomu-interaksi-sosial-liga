package id.ac.ui.cs.advprog.yomuliga.service;

import id.ac.ui.cs.advprog.yomuliga.model.Clan;
import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import id.ac.ui.cs.advprog.yomuliga.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomuliga.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        memberRepository.save(member);
    }
}