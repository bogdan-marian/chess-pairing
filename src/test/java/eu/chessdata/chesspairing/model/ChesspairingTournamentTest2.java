package eu.chessdata.chesspairing.model;

import org.junit.BeforeClass;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class ChesspairingTournamentTest2 {
    @BeforeClass
    public static void checkGeneratedFilesFolder() {
        TestUtils.createIfNotPresentGeneratedFilesFolder();
    }

    @Test
    public void testNoGamesGenerated() {
        ChesspairingTournament tournament = TestUtils.loadFile(
                "/chesspairingTournamentTest2/testNoGamesGenerated.json");
        assertNotNull(tournament);
        assertEquals(3, tournament.getRounds().size());

    }
}
