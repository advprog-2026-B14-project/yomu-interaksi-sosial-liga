package id.ac.ui.cs.advprog.yomuliga.service.strategy.modifier;


import id.ac.ui.cs.advprog.yomuliga.client.LearningClient;
import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import id.ac.ui.cs.advprog.yomuliga.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LowAccuracyDebuffStrategyTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LearningClient learningClient;

    private LowAccuracyDebuffStrategy strategy;

    private final String clanId = UUID.randomUUID().toString();
    private final UUID userId1 = UUID.randomUUID();
    private final UUID userId2 = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        strategy = new LowAccuracyDebuffStrategy(memberRepository, learningClient, 50.0, 0.8);
    }

    @Test
    void testIsApplicable_TrueWhenAverageBelowThreshold() {
        ClanMember member1 = new ClanMember();
        member1.setUserId(userId1);
        ClanMember member2 = new ClanMember();
        member2.setUserId(userId2);

        when(memberRepository.findAllByClanId(UUID.fromString(clanId))).thenReturn(List.of(member1, member2));

        // Akurasi jelek: (40 + 30) / 2 = 35% (Di bawah 50%)
        when(learningClient.getStudentAccuracy(userId1.toString())).thenReturn(40.0);
        when(learningClient.getStudentAccuracy(userId2.toString())).thenReturn(30.0);

        assertTrue(strategy.isApplicable(clanId));
    }

    @Test
    void testIsApplicable_FalseWhenAverageAboveThreshold() {
        ClanMember member1 = new ClanMember();
        member1.setUserId(userId1);

        when(memberRepository.findAllByClanId(UUID.fromString(clanId))).thenReturn(List.of(member1));

        // Akurasi bagus: 80%
        when(learningClient.getStudentAccuracy(userId1.toString())).thenReturn(80.0);

        assertFalse(strategy.isApplicable(clanId));
    }

    @Test
    void testIsApplicable_FalseWhenNoMembers() {
        when(memberRepository.findAllByClanId(UUID.fromString(clanId))).thenReturn(List.of());
        assertFalse(strategy.isApplicable(clanId)); // Kalau klan kosong, nggak kena hukum
    }

    @Test
    void testIsApplicable_FalseWhenExceptionThrown() {
        when(memberRepository.findAllByClanId(UUID.fromString(clanId))).thenThrow(new RuntimeException("API Down"));
        assertFalse(strategy.isApplicable(clanId));
    }

    @Test
    void testApplyModifier() {
        double currentScore = 100.0;
        double expectedScore = 80.0; // 100 * 0.8
        assertEquals(expectedScore, strategy.applyModifier(currentScore));
    }
}