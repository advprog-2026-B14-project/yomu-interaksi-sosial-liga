package id.ac.ui.cs.advprog.yomuliga.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
import java.time.OffsetDateTime;

@Entity
@Getter
@Setter
@Table(name = "clan_members", schema = "liga_mod")
public class ClanMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "clan_id", nullable = false)
    private UUID clanId;

    @Column(name = "user_id", unique = true, nullable = false)
    private UUID userId;

    @Column(name = "role")
    private String role = "ANGGOTA";

    @Column(name = "joined_at", insertable = false, updatable = false)
    private OffsetDateTime joinedAt;
}