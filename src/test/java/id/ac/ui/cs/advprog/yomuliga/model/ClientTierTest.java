package id.ac.ui.cs.advprog.yomuliga.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClanTierTest {

    @Test
    void testFromStringValid() {
        assertEquals(ClanTier.BRONZE, ClanTier.fromString("BRONZE"));
        assertEquals(ClanTier.SILVER, ClanTier.fromString("silver"));
        assertEquals(ClanTier.GOLD, ClanTier.fromString(" GoLd "));
    }

    @Test
    void testFromStringInvalid() {
        assertEquals(ClanTier.BRONZE, ClanTier.fromString("KAYU"));
    }

    @Test
    void testFromStringNullOrEmpty() {
        assertEquals(ClanTier.BRONZE, ClanTier.fromString(null));
        assertEquals(ClanTier.BRONZE, ClanTier.fromString(""));
    }

    @Test
    void testNext() {
        assertEquals(ClanTier.SILVER, ClanTier.BRONZE.next());
        assertEquals(ClanTier.GOLD, ClanTier.SILVER.next());
        assertEquals(ClanTier.DIAMOND, ClanTier.GOLD.next());
    }

    @Test
    void testNextMentokDiamond() {
        // Diamond nggak bisa naik tier lagi
        assertNull(ClanTier.DIAMOND.next());
    }

    @Test
    void testPrevious() {
        assertEquals(ClanTier.GOLD, ClanTier.DIAMOND.previous());
        assertEquals(ClanTier.SILVER, ClanTier.GOLD.previous());
        assertEquals(ClanTier.BRONZE, ClanTier.SILVER.previous());
    }

    @Test
    void testPreviousMentokBronze() {
        assertNull(ClanTier.BRONZE.previous());
    }
}