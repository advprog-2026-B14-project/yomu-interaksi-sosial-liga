package id.ac.ui.cs.advprog.yomuliga.service;

import id.ac.ui.cs.advprog.yomuliga.model.Clan;

import java.util.UUID;

public interface ClanService {
    Clan createClan(String name, UUID leaderId);
    void joinClan(UUID clanId, UUID userId);
}