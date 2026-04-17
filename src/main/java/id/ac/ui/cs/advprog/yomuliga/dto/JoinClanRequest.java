package id.ac.ui.cs.advprog.yomuliga.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class JoinClanRequest {
    private UUID clanId;
    private UUID userId;
}