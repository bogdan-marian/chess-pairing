package eu.chessdata.chesspairing.model;

import eu.chessdata.chesspairing.algoritms.fideswissduch.Algorithm;
import eu.chessdata.chesspairing.algoritms.javafo.JavafoWrapp;
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
        Algorithm algorithm = new JavafoWrapp();
        tournament = algorithm.generateNextRound(tournament);

        assertEquals(4, tournament.getRounds().size());
    }
}
