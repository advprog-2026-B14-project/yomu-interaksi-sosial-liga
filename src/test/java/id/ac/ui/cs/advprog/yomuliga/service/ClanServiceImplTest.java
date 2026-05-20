package id.ac.ui.cs.advprog.yomuliga.service;

import id.ac.ui.cs.advprog.yomuliga.model.Clan;
import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import id.ac.ui.cs.advprog.yomuliga.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomuliga.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClanServiceImplTest {

    @Mock
    private ClanRepository clanRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private ClanServiceImpl clanService;

    private Clan dummyClan;
    private UUID dummyClanId;
    private UUID dummyUserId;

    @BeforeEach
    void setUp() {
        dummyClanId = UUID.randomUUID();
        dummyUserId = UUID.randomUUID();

        dummyClan = new Clan();
        dummyClan.setId(dummyClanId);
        dummyClan.setNamaClan("Fasilkom Elite");
        dummyClan.setIdKetua(dummyUserId);
        dummyClan.setTotalSkor(0.0);
        dummyClan.setTier("BRONZE");
    }

    @Test
    void testCreateClanSuccess() {
        when(clanRepository.findByNamaClan("Fasilkom Elite")).thenReturn(Optional.empty());
        when(clanRepository.save(any(Clan.class))).thenReturn(dummyClan);

       Clan result = clanService.createClan("Fasilkom Elite", dummyUserId);

        assertNotNull(result);
        assertEquals("Fasilkom Elite", result.getNamaClan());
        assertEquals("BRONZE", result.getTier());

        verify(clanRepository, times(1)).save(any(Clan.class));
    }

    @Test
    void testJoinClanGagalKarenaSudahPunyaClan() {
        when(clanRepository.findById(dummyClanId)).thenReturn(Optional.of(dummyClan));

        ClanMember existingMember = new ClanMember();
        when(memberRepository.findByUserId(dummyUserId)).thenReturn(Optional.of(existingMember));

        Exception exception = assertThrows(RuntimeException.class, () -> clanService.joinClan(dummyClanId, dummyUserId));

        assertEquals("Kamu sudah bergabung dengan Clan lain!", exception.getMessage());

        verify(memberRepository, never()).save(any(ClanMember.class));
    }

    @Test
    void testGetLeaderboardByTier() {
        List<Clan> mockList = List.of(dummyClan);

        when(clanRepository.findAllByTierOrderByTotalSkorDesc("BRONZE")).thenReturn(mockList);

        List<Clan> result = clanService.getLeaderboardByTier("bronze");

        assertFalse(result.isEmpty());
        assertEquals("Fasilkom Elite", result.getFirst().getNamaClan());
        verify(clanRepository, times(1)).findAllByTierOrderByTotalSkorDesc("BRONZE");
    }
}