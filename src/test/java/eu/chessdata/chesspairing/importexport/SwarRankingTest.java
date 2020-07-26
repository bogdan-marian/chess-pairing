package eu.chessdata.chesspairing.importexport;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.chessdata.chesspairing.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SwarRankingTest {

    @Test
    public void loadExport01NoPairingsTest() throws IOException {
        InputStream inputStream = SwarRankingTest.class.getResourceAsStream(
                "/importexport/swar/Swar-export-01-no-parings.json");

        ObjectMapper mapper = new ObjectMapper();
        Swar swar = new Swar();
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);
        Assert.assertTrue(tournament.getName().equals("Chessout-export"));
        ChesspairingPlayer bogdan = tournament.getPlayerById("10001");
        Assert.assertTrue(bogdan.getName().equals("Bogdan"));

        Assert.assertTrue(tournament.getTotalRounds() == 4);

        int pairedRounds = tournament.getRounds().size();
        Assert.assertEquals(0, pairedRounds);


        List<ChesspairingStanding> standings = tournament.getStandings();
        Assert.assertEquals(8, standings.size());
        for (ChesspairingStanding standing : standings) {
            if (standing.getRank() == 1) {
                ChesspairingPlayer catalin = standing.getPlayer();
                Assert.assertEquals("Catalin", catalin.getName());
                Assert.assertEquals("10006", catalin.getPlayerKey());

                Assert.assertEquals(0f, standing.getDirectEncounter(), 1e-8);

            }
        }
    }


    @Test
    public void loadExport02ParingsTest() throws IOException {
        InputStream inputStream = SwarRankingTest.class.getResourceAsStream(
                "/importexport/swar/Swar-export-02-parings.json");

        ObjectMapper mapper = new ObjectMapper();
        Swar swar = new Swar();
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);
        Assert.assertTrue(tournament.getName().equals("Chessout-export"));
        ChesspairingPlayer bogdan = tournament.getPlayerById("10001");
        Assert.assertTrue(bogdan.getName().equals("Bogdan"));

        Assert.assertTrue(tournament.getTotalRounds() == 4);

        int pairedRounds = tournament.getRounds().size();
        Assert.assertEquals(2, pairedRounds);

        // round 1 game
        ChesspairingGame game = tournament.getRoundByRoundNumber(1).getGame(bogdan);
        ChesspairingPlayer adrian = game.getWhitePlayer();
        Assert.assertEquals("Adrian", adrian.getName());

        Assert.assertEquals(ChesspairingResult.WHITE_WINS, game.getResult());

        // round 2 game
        game = tournament.getRoundByRoundNumber(2).getGame(bogdan);
        ChesspairingPlayer rares = game.getBlackPlayer();
        Assert.assertEquals("Cristina", rares.getName());
        Assert.assertEquals(ChesspairingResult.NOT_DECIDED, game.getResult());


        List<ChesspairingStanding> standings = tournament.getStandings();
        Assert.assertEquals(8, standings.size());
        for (ChesspairingStanding standing : standings) {
            if (standing.getRank() == 7) {
                ChesspairingPlayer catalin = standing.getPlayer();
                Assert.assertEquals("Catalin", catalin.getName());
                Assert.assertEquals("10006", catalin.getPlayerKey());

                Assert.assertEquals(0f, standing.getDirectEncounter(), 1e-8);
                Assert.assertEquals(1.00, standing.getBucholtzCut1(), 1e-8);
            }
        }
    }

    @Test
    public void loadExport02ResultsTest() throws IOException {
        InputStream inputStream = SwarRankingTest.class.getResourceAsStream(
                "/importexport/swar/Swar-export-02-results.json");

        ObjectMapper mapper = new ObjectMapper();
        Swar swar = new Swar();
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);
        Assert.assertTrue(tournament.getName().equals("Chessout-export"));
        ChesspairingPlayer bogdan = tournament.getPlayerById("10001");
        Assert.assertTrue(bogdan.getName().equals("Bogdan"));

        Assert.assertTrue(tournament.getTotalRounds() == 4);

        int pairedRounds = tournament.getRounds().size();
        Assert.assertEquals(2, pairedRounds);

        // round 1 game
        ChesspairingGame game = tournament.getRoundByRoundNumber(1).getGame(bogdan);
        ChesspairingPlayer adrian = game.getWhitePlayer();
        Assert.assertEquals("Adrian", adrian.getName());

        Assert.assertEquals(ChesspairingResult.WHITE_WINS, game.getResult());

        // round 2 game
        game = tournament.getRoundByRoundNumber(2).getGame(bogdan);
        ChesspairingPlayer rares = game.getBlackPlayer();
        Assert.assertEquals("Cristina", rares.getName());
        Assert.assertEquals(ChesspairingResult.WHITE_WINS, game.getResult());
    }
}