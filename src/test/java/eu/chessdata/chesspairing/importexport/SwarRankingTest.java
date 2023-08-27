package eu.chessdata.chesspairing.importexport;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.chessdata.chesspairing.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SwarRankingTest {

    @Test
    public void loadExport01NoPairingsTest() throws IOException {
        InputStream inputStream = SwarRankingTest.class.getResourceAsStream(
                "/importexport/swar/Swar-export-01-no-parings.json");

        ObjectMapper mapper = new ObjectMapper();
        Swar swar = Swar.newInstance();
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);
        Assert.assertTrue(tournament.getName()
                .equals("Chessout-export"));
        ChesspairingPlayer bogdan = tournament.getPlayerById("10001");
        Assert.assertTrue(bogdan.getName()
                .equals("Bogdan"));

        Assert.assertTrue(tournament.getTotalRounds() == 4);

        int pairedRounds = tournament.getRounds()
                .size();
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
        Swar swar = Swar.newInstance();
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);
        Assert.assertTrue(tournament.getName()
                .equals("Chessout-export"));
        ChesspairingPlayer bogdan = tournament.getPlayerById("10001");
        Assert.assertTrue(bogdan.getName()
                .equals("Bogdan"));

        Assert.assertTrue(tournament.getTotalRounds() == 4);

        int pairedRounds = tournament.getRounds()
                .size();
        Assert.assertEquals(2, pairedRounds);

        // round 1 game
        ChesspairingGame game = tournament.getRoundByRoundNumber(1)
                .getGame(bogdan);
        ChesspairingPlayer adrian = game.getWhitePlayer();
        Assert.assertEquals("Adrian", adrian.getName());

        Assert.assertEquals(ChesspairingResult.WHITE_WINS, game.getResult());

        // round 2 game
        game = tournament.getRoundByRoundNumber(2)
                .getGame(bogdan);
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
        Swar swar = Swar.newInstance();
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);
        Assert.assertTrue(tournament.getName()
                .equals("Chessout-export"));
        ChesspairingPlayer bogdan = tournament.getPlayerById("10001");
        Assert.assertTrue(bogdan.getName()
                .equals("Bogdan"));

        Assert.assertTrue(tournament.getTotalRounds() == 4);

        int pairedRounds = tournament.getRounds()
                .size();
        Assert.assertEquals(2, pairedRounds);

        // round 1 game
        ChesspairingGame game = tournament.getRoundByRoundNumber(1)
                .getGame(bogdan);
        ChesspairingPlayer adrian = game.getWhitePlayer();
        Assert.assertEquals("Adrian", adrian.getName());

        Assert.assertEquals(ChesspairingResult.WHITE_WINS, game.getResult());

        // round 2 game
        game = tournament.getRoundByRoundNumber(2)
                .getGame(bogdan);
        ChesspairingPlayer rares = game.getBlackPlayer();
        Assert.assertEquals("Cristina", rares.getName());
        Assert.assertEquals(ChesspairingResult.WHITE_WINS, game.getResult());

        List<ChesspairingStanding> standings = tournament.getStandings();
        Assert.assertEquals(8, standings.size());
        for (ChesspairingStanding standing : standings) {
            if (standing.getRank() == 5) {
                ChesspairingPlayer catalin = standing.getPlayer();
                Assert.assertEquals("Bogdan", catalin.getName());
                Assert.assertEquals("10001", catalin.getPlayerKey());

                Assert.assertEquals(0f, standing.getDirectEncounter(), 1e-8);
                Assert.assertEquals(1.00, standing.getBucholtzCut1(), 1e-8);
                Assert.assertEquals(1, standing.getGamesPlayedWithBlack());
            }
        }
    }

    @Test
    public void doubleForfeitTest() throws IOException {
        InputStream inputStream = SwarRankingTest.class.getResourceAsStream(
                "/importexport/swar/Swar-ranking-double-forfeit.json");

        ObjectMapper mapper = new ObjectMapper();
        Swar swar = Swar.newInstance();
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);
        tournament.getRounds()
                .get(0)
                .getGames()
                .get(1)
                .setResult(ChesspairingResult.DRAW_DOUBLE_FORFEIT);//
        Assert.assertNotNull(tournament);

        int roundNumber = 2;
        List<ChesspairingPlayer> standingsList = tournament.computeStandings(roundNumber);
        Assert.assertNotNull(standingsList);
        Map<String, Integer> rankMap = new HashMap<>();
        Map<String, Float> pointsMap = new HashMap<>();
        Map<String, Float> bucholzPointsMap = new HashMap<>();
        int rank = 1;
        for (ChesspairingPlayer payer : standingsList) {
            rankMap.put(payer.getPlayerKey(), ++rank);
            pointsMap.put(payer.getPlayerKey(), tournament.computePoints(roundNumber, payer.getPlayerKey()));
            bucholzPointsMap.put(payer.getPlayerKey(),
                    tournament.computeBuchholzPoints(roundNumber, payer.getPlayerKey()));
        }
        String playerId = "10001";
        Integer rankVal = rankMap.get(playerId);
        Float pointsVal = pointsMap.get(playerId);
        Float buchholzVal = bucholzPointsMap.get(playerId);
        Assert.assertEquals(Integer.valueOf(6), rankVal);
        Assert.assertEquals(Float.valueOf(0), pointsVal);
        Assert.assertEquals(Float.valueOf(0.5F), buchholzVal);
    }
}