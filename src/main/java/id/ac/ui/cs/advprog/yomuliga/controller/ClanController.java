package id.ac.ui.cs.advprog.yomuliga.controller;

import id.ac.ui.cs.advprog.yomuliga.model.Clan;
import id.ac.ui.cs.advprog.yomuliga.service.ClanService;
import id.ac.ui.cs.advprog.yomuliga.dto.JoinClanRequest;
import id.ac.ui.cs.advprog.yomuliga.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomuliga.dto.ScoreRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/liga")
public class ClanController {

    @Autowired
    private ClanService clanService;

    @PostMapping("/clan/create")
    public ResponseEntity<?> createClan(@RequestBody CreateClanRequest request) {
        try {
            Clan newClan = clanService.createClan(request.getNama(), request.getKetuaId());
            return ResponseEntity.ok(newClan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/clan/join")
    public ResponseEntity<?> joinClan(@RequestBody JoinClanRequest request) {
        try {
            clanService.joinClan(request.getClanId(), request.getUserId());
            return ResponseEntity.ok(Map.of("message", "Berhasil bergabung ke clan!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/add-score")
    public ResponseEntity<?> addScore(@RequestBody ScoreRequest request) {
        clanService.addScoreToMember(request.getClanId(), request.getUserId(), request.getPoints());
        return ResponseEntity.ok("Skor berhasil ditambahkan!");
    }
}