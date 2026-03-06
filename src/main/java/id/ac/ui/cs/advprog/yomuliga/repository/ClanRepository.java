package id.ac.ui.cs.advprog.yomuliga.repository;
import id.ac.ui.cs.advprog.yomuliga.model.Clan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface ClanRepository extends JpaRepository<Clan, UUID> {

    Optional<Clan> findByNamaClan(String namaClan);

    Optional<Clan> findByIdKetua(UUID idKetua);
}
