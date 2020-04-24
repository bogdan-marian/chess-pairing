package eu.chessdata.chesspairing.model;

import com.google.gson.Gson;
import eu.chessdata.chesspairing.Api;
import eu.chessdata.chesspairing.tools.Tools;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;


public class ChesspairingTournamentTest {
    @BeforeClass
    public static void checGeneratedFilesFolder() {
        TestUtils.createIfNotPresentGeneratedFilesFolder();
    }

    /**
     * basic serialize and deserialize of a tournament
     */
    @Test
    public void simpetBuildTournament() {
        ChesspairingTournament tournament = TestUtils.buildTournament("Simple chess tournament");
        String stringTournament = Api.serializeTournament(tournament);
        ChesspairingTournament secondTournament = Api.deserializeTournament(stringTournament);
        Assert.assertTrue("Tournament names should be the same",
                tournament.getName().equals(secondTournament.getName()));
        String secondString = Api.serializeTournament(secondTournament);
        Assert.assertTrue("The hole serialized tournament should be the same", stringTournament.equals(secondString));
    }

    /**
     * Just as the name implies this is just a simple snippet that shows a simple
     * way to create a {@link ChesspairingTournament} object from a json file.
     *
     * @throws UnsupportedEncodingException
     */
    @Test
    public void loadFromFileSnipet() throws UnsupportedEncodingException {
        InputStream inputStream = ChesspairingTournamentTest.class
                .getResourceAsStream("/chesspairingTournamentTest/tournament1.json");
        Reader reader = new InputStreamReader(inputStream, "UTF-8");
        Gson gson = Tools.getGson();

        // line that loads from file
        ChesspairingTournament tournament = gson.fromJson(reader, ChesspairingTournament.class);

        // simple line that tests that wee have the correct data
        Assert.assertTrue("Not the expected data", tournament.getName().equals("Tournament 1"));
    }

    /**
     * Simple test meant to be used to build specific tournaments and then save them
     * in files. The java.io package will only be used in tests. No java.io.File
     * objects will be used the bye chessparing package.
     *
     * @throws IOException
     */
    @Test
    public void constumizeTournament() throws IOException {

        String tournament1FilePath = Tools.GENERATED_FILES + "/tournament1.json";
        Writer writer = new FileWriter(tournament1FilePath);
        Gson gson = Tools.getGson();

        ChesspairingTournament tournament = TestUtils.buildTournament("Tournament 1");

        // write tournament to file
        gson.toJson(tournament, writer);
        writer.close();
    }

    @Test
    public void test1ComputeStandings() throws IOException {
        ChesspairingTournament tournament = TestUtils.loadFile("/model/test1ComputeStandings.json");
        assertNotNull("Tournament object is null", tournament);

        for (ChesspairingRound round : tournament.getRounds()) {
            int roundNumber = round.getRoundNumber();
            List<ChesspairingPlayer> standings = tournament.computeStandings(roundNumber);
            assert (standings.size() > 0);
        }

    }

    @Test
    public void testGetBuchholzPoints() {

        ChesspairingTournament tournament = new ChesspairingTournament();
        ChesspairingPlayer p1G = TestUtils.buildPlayer("1G", 1000);
        ChesspairingPlayer p2A = TestUtils.buildPlayer("2A", 1000);
        ChesspairingPlayer p3B = TestUtils.buildPlayer("3B", 1000);
        ChesspairingPlayer p4F = TestUtils.buildPlayer("4F", 1000);
        ChesspairingPlayer p5E = TestUtils.buildPlayer("5E", 1000);
        ChesspairingPlayer p6D = TestUtils.buildPlayer("6D", 1000);
        ChesspairingPlayer p7C = TestUtils.buildPlayer("7C", 1000);
        tournament.setPlayers(Arrays.asList(p1G, p2A, p3B, p4F, p5E, p6D, p7C));
        assertEquals(7, tournament.getPlayers().size());

        ChesspairingRound round1 = new ChesspairingRound();
        round1.setGames(
                Arrays.asList(
                        TestUtils.buildGame(1, p1G, null, ChesspairingResult.BYE),
                        TestUtils.buildGame(2, p6D, p2A, ChesspairingResult.BLACK_WINS),
                        TestUtils.buildGame(3, p3B, p5E, ChesspairingResult.WHITE_WINS_BY_FORFEIT),
                        TestUtils.buildGame(4, p4F, p7C, ChesspairingResult.WHITE_WINS)
                )
        );
        round1.setAbsentPlayers(new ArrayList<ChesspairingPlayer>());


        tournament.setRounds(
                Arrays.asList(round1)
        );

        assertEquals(1.5f, tournament.computeBuchholzPoints(1, p1G.getPlayerKey()));

    }
}
