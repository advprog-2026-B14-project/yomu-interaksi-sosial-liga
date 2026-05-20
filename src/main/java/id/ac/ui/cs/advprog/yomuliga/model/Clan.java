package id.ac.ui.cs.advprog.yomuliga.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "clans", schema = "liga_mod")
public class Clan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "nama_clan", unique = true, nullable = false)
    private String namaClan;

    @Column(name = "id_ketua", nullable = false)
    private UUID idKetua;

    private String tier = "BRONZE";

    @Column(name = "total_skor")
    private Double totalSkor = 0.0;

    public void addSkor(double points) {
        this.totalSkor += points;
        updateTier();
    }

    public void updateTier() {
        if (this.totalSkor > 1000) {
            this.tier = "DIAMOND";
        } else if (this.totalSkor > 500) {
            this.tier = "GOLD";
        } else if (this.totalSkor > 100) {
            this.tier = "SILVER";
        } else {
            this.tier = "BRONZE";
        }
    }
}
