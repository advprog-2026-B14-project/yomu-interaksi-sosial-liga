package id.ac.ui.cs.advprog.yomuliga.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ScoreRequest {
    private UUID clanId;
    private UUID userId;
    private double points;
}

