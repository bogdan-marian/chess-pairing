package eu.chessdata.chesspairing.importexport;


import eu.chessdata.chesspairing.model.*;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SwarTest {

    @Test
    public void loadExport01NoPairingsTest() throws IOException {
        InputStream inputStream = SwarTest.class.getResourceAsStream(
                "/importexport/swar/Swar-export-01-no-parings.json");

        Swar swar = Swar.newInstance("NationalId");
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);
        Assert.assertTrue(tournament.getName().equals("Chessout-export"));
        ChesspairingPlayer bogdan = tournament.getPlayerById("10001");
        Assert.assertTrue(bogdan.getName().equals("Bogdan"));

        Assert.assertTrue(tournament.getTotalRounds() == 4);

        int pairedRounds = tournament.getRounds().size();
        Assert.assertEquals(0, pairedRounds);
    }

    @Test
    public void loadExport01PairingsTest() throws IOException {
        InputStream inputStream = SwarTest.class.getResourceAsStream(
                "/importexport/swar/Swar-export-01-parings.json");

        Swar swar = Swar.newInstance("NationalId");
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);
        Assert.assertTrue(tournament.getName().equals("Chessout-export"));
        ChesspairingPlayer bogdan = tournament.getPlayerById("10001");
        Assert.assertTrue(bogdan.getName().equals("Bogdan"));

        Assert.assertTrue(tournament.getTotalRounds() == 4);

        int pairedRounds = tournament.getRounds().size();
        Assert.assertEquals(1, pairedRounds);


        ChesspairingGame game = tournament.getRoundByRoundNumber(1).getGame(bogdan);
        ChesspairingPlayer adrian = game.getWhitePlayer();
        Assert.assertEquals("Adrian", adrian.getName());

        Assert.assertEquals(ChesspairingResult.NOT_DECIDED, game.getResult());

    }

    @Test
    public void loadExport01ResultsTest() throws IOException {
        InputStream inputStream = SwarTest.class.getResourceAsStream(
                "/importexport/swar/Swar-export-01-results.json");

        Swar swar = Swar.newInstance("NationalId");
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);
        Assert.assertTrue(tournament.getName().equals("Chessout-export"));
        ChesspairingPlayer bogdan = tournament.getPlayerById("10001");
        Assert.assertTrue(bogdan.getName().equals("Bogdan"));

        Assert.assertTrue(tournament.getTotalRounds() == 4);

        int pairedRounds = tournament.getRounds().size();
        Assert.assertEquals(1, pairedRounds);

        ChesspairingGame game = tournament.getRoundByRoundNumber(1).getGame(bogdan);
        ChesspairingPlayer adrian = game.getWhitePlayer();
        Assert.assertEquals("Adrian", adrian.getName());

        Assert.assertEquals(ChesspairingResult.WHITE_WINS, game.getResult());
    }

    @Test
    public void loadExport02ParingsTest() throws IOException {
        InputStream inputStream = SwarTest.class.getResourceAsStream(
                "/importexport/swar/Swar-export-02-parings.json");

        Swar swar = Swar.newInstance("NationalId");
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
    }

    @Test
    public void loadExport02ResultsTest() throws IOException {
        InputStream inputStream = SwarTest.class.getResourceAsStream(
                "/importexport/swar/Swar-export-02-results.json");

        Swar swar = Swar.newInstance("NationalId");
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

    @Test
    public void loadExportBraineEchecsInterneTest() throws IOException {
        InputStream inputStream = SwarTest.class.getResourceAsStream(
                "/importexport/swar/Braine-echecs-interne.json");

        Swar swar = Swar.newInstance("NationalId");
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);
        Assert.assertEquals("Braine échecs interne", tournament.getName());

        ChesspairingPlayer smirnov = tournament.getPlayerById("17257");
        Assert.assertEquals("Smirnov, Stepan", smirnov.getName());

        ChesspairingPlayer vanlaer = tournament.getPlayerById("21560");
        Assert.assertEquals("Vanlaer, Typhene", vanlaer.getName());

        ChesspairingRound round7 = tournament.getRoundByRoundNumber(7);
        ChesspairingGame game14 = round7.getGame(vanlaer);
        ChesspairingPlayer colmont = game14.getBlackPlayer();
        Assert.assertEquals("Colmont Aloïs", colmont.getName());
        Assert.assertEquals(ChesspairingResult.BLACK_WINS, game14.getResult());
    }

    @Test
    public void noResultsBugTest() throws IOException {
        InputStream inputStream = SwarTest.class.getResourceAsStream(
                "/importexport/swar/Swar-export-GameResultBug.json");

        Swar swar = Swar.newInstance("NationalId");
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);

        ChesspairingRound round = tournament.getRoundByRoundNumber(1);
        List<ChesspairingGame> games = round.getGames();
        for (ChesspairingGame game : games) {
            Assert.assertNotEquals(ChesspairingResult.NOT_DECIDED, game.getResult());
        }
    }
}