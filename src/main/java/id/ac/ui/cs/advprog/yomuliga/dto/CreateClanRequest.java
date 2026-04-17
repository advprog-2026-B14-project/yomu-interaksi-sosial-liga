package id.ac.ui.cs.advprog.yomuliga.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class CreateClanRequest {
    private String nama;
    private UUID ketuaId;
}