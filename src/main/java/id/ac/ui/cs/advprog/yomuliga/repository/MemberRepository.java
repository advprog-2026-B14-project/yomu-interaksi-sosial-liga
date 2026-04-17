package id.ac.ui.cs.advprog.yomuliga.repository;

import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<ClanMember, Integer> {
    Optional<ClanMember> findByUserId(UUID userId);
    List<ClanMember> findAllByClanId(UUID clanId);
    Optional<ClanMember> findByClanIdAndUserId(UUID clanId, UUID userId);
}