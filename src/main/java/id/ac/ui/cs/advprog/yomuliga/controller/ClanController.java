package id.ac.ui.cs.advprog.yomuliga.controller;

import id.ac.ui.cs.advprog.yomuliga.model.Clan;
import id.ac.ui.cs.advprog.yomuliga.service.ClanService;
import id.ac.ui.cs.advprog.yomuliga.dto.JoinClanRequest;
import id.ac.ui.cs.advprog.yomuliga.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomuliga.dto.ScoreRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/liga")
@CrossOrigin(origins = {"http://localhost:3000", "https://yomu-frontend-zeta.vercel.app"})
public class ClanController {

    private static final String MESSAGE_KEY = "message";

    private final ClanService clanService;
    public ClanController(ClanService clanService) {
        this.clanService = clanService;
    }

    @PostMapping("/clan/create")
    public ResponseEntity<Object> createClan(@RequestBody CreateClanRequest request) {
        try {
            Clan newClan = clanService.createClan(request.getNama(), request.getKetuaId());
            return ResponseEntity.ok(newClan);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/clan/join")
    public ResponseEntity<Map<String, String>> joinClan(@RequestBody JoinClanRequest request) {
        try {
            clanService.joinClan(request.getClanId(), request.getUserId());
            return ResponseEntity.ok(Map.of("message", "Berhasil bergabung ke clan!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/add-score")
    public ResponseEntity<String> addScore(@RequestBody ScoreRequest request) {
        clanService.addScoreToMember(request.getClanId(), request.getUserId(), request.getPoints());
        return ResponseEntity.ok("Skor berhasil ditambahkan!");
    }

    @PostMapping("/admin/end-season")
    public ResponseEntity<Map<String, String>> endSeason() {
        clanService.endOfSeason();
        return ResponseEntity.ok(Map.of("message", "Season ended successfully"));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<Clan>> getLeaderboard(@RequestParam(required = false) String tier) {
        if (tier != null && !tier.trim().isEmpty()) {
            return ResponseEntity.ok(clanService.getLeaderboardByTier(tier));
        }
        return ResponseEntity.ok(clanService.getAllLeaderboard());
    }
}