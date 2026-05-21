package id.ac.ui.cs.advprog.yomuliga.model; // sesuaikan packagenya

public enum ClanTier {
    BRONZE,
    SILVER,
    GOLD,
    DIAMOND;

    public static ClanTier fromString(String tier) {
        if (tier == null || tier.trim().isEmpty()) {
            return BRONZE;
        }
        try {
            return ClanTier.valueOf(tier.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return BRONZE;
        }
    }

    public ClanTier next() {
        int currentIndex = this.ordinal();
        if (currentIndex < values().length - 1) {
            return values()[currentIndex + 1];
        }
        return null;
    }

    public ClanTier previous() {
        int currentIndex = this.ordinal();
        if (currentIndex > 0) {
            return values()[currentIndex - 1];
        }
        return null;
    }
}