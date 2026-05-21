package id.ac.ui.cs.advprog.yomuliga.service.strategy.modifier;

import id.ac.ui.cs.advprog.yomuliga.client.LearningClient;
import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import id.ac.ui.cs.advprog.yomuliga.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class LowAccuracyDebuffStrategy implements ScoreModifierStrategy {

	private static final Logger log = LoggerFactory.getLogger(LowAccuracyDebuffStrategy.class);

	private final MemberRepository memberRepository;
	private final LearningClient learningClient;

	private final double accuracyThreshold;

	private final double debuffFactor;

	public LowAccuracyDebuffStrategy(MemberRepository memberRepository,
									 LearningClient learningClient,
									 @Value("${learning.accuracy.threshold:50.0}") double accuracyThreshold,
									 @Value("${learning.debuff.factor:0.8}") double debuffFactor) {
		this.memberRepository = memberRepository;
		this.learningClient = learningClient;
		this.accuracyThreshold = accuracyThreshold;
		this.debuffFactor = debuffFactor;
	}

	@Override
	public boolean isApplicable(String clanId) {
		try {
			List<ClanMember> members = memberRepository.findAllByClanId(UUID.fromString(clanId));

			if (members == null || members.isEmpty()) {
				return false;
			}

			double totalAccuracy = 0.0;
			int validMemberCount = 0;

			for (ClanMember member : members) {
				if (member.getUserId() == null) continue;

				double accuracy = learningClient.getStudentAccuracy(member.getUserId().toString());
				totalAccuracy += accuracy;
				validMemberCount++;
			}

			if (validMemberCount == 0) return false;

			double averageAccuracy = totalAccuracy / validMemberCount;
			log.debug("Rata-rata akurasi klan {} = {}", clanId, averageAccuracy);

			return averageAccuracy < accuracyThreshold;

		} catch (Exception ex) {
			log.warn("Gagal menghitung rata-rata akurasi klan {}: {}", clanId, ex.getMessage());
			return false;
		}
	}

	@Override
	public double applyModifier(double currentScore) {
		return currentScore * debuffFactor;
	}
}