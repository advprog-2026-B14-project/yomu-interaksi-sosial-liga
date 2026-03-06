package id.ac.ui.cs.advprog.yomuliga.repository;

import id.ac.ui.cs.advprog.yomuliga.model.ClanMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<ClanMember, Integer> {
    Optional<ClanMember> findByUserId(UUID userId);
}