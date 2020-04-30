package eu.chessdata.chesspairing.algoritms.javafo;

import eu.chessdata.chesspairing.algoritms.fideswissduch.Algorithm;
import eu.chessdata.chesspairing.model.ChesspairingPlayer;
import eu.chessdata.chesspairing.model.ChesspairingTournament;
import eu.chessdata.chesspairing.model.TestUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
                "/jafafoWrapp/testNoGamesGenerated.json");
        assertNotNull(tournament);
        assertEquals(3, tournament.getRounds().size());
        Algorithm algorithm = new JavafoWrapp();

        Comparator<ChesspairingPlayer> comparator = new Comparator<ChesspairingPlayer>() {
            @Override
            public int compare(ChesspairingPlayer player, ChesspairingPlayer t1) {
                return -1 * (player.getElo() - t1.getElo());
            }
        };
        List<ChesspairingPlayer> players = tournament.getPlayers();
        System.out.println(players);
        Collections.sort(players, comparator);
        System.out.println(players);

        tournament.setPlayers(players);

        tournament = algorithm.generateNextRound(tournament);

        assertEquals(4, tournament.getRounds().size());
    }
}
