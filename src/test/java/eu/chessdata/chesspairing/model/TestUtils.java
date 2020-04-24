package eu.chessdata.chesspairing.model;

import com.google.gson.Gson;
import eu.chessdata.chesspairing.tools.Tools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestUtils {
    /**
     * Create the generatedFiles folder in the rout of the project this folder
     * is ignored by git and is meant to put the generated files while building
     * this project
     */
    public static void createIfNotPresentGeneratedFilesFolder() {

        File generatedFiles = new File(Tools.GENERATED_FILES);
        String absolutePath = generatedFiles.getAbsolutePath();
        if (generatedFiles.exists()) {
            return;
        }
        boolean folderCreated = generatedFiles.mkdirs();
        if (!folderCreated) {
            throw new IllegalStateException("Not able to create folder: " + absolutePath);
        }
        return;
    }

    public static ChesspairingTournament buildTournament(String tournamentName) {
        ChesspairingTournament tournament = new ChesspairingTournament();
        tournament.setName(tournamentName);
        tournament.setPlayers(buildPlayers());
        return tournament;
    }

    /**
     * it builds a tournament with 23 players no games
     *
     * @return
     */
    public static ChesspairingTournament buildTournament23() {
        ChesspairingTournament tournament = new ChesspairingTournament();

        List<ChesspairingPlayer> players = new ArrayList<ChesspairingPlayer>();
        // <star players>
        players.add(buildPlayer("Bogdan Marian", 1450));
        players.add(buildPlayer("Lacramioara", 1320));
        players.add(buildPlayer("Calin Constantin", 1550));
        players.add(buildPlayer("Cezar Andrei", 1200));
        players.add(buildPlayer("Rares", 1600));
        players.add(buildPlayer("Catalin Ioan", 1750));
        players.add(buildPlayer("Maria", 1700));
        players.add(buildPlayer("Delia Ioana", 1750));
        players.add(buildPlayer("Cemil", 2500));
        players.add(buildPlayer("Delia Dinu", 700));
        players.add(buildPlayer("Mihai", 600));
        players.add(buildPlayer("Mihaela", 750));
        // </star players>
        int sead = 500;
        for (int i = 1; i <= 11; i++) {
            int elo = sead + (i * 5);
            String player = "Player_" + i;
            players.add(buildPlayer(player, elo));
        }

        tournament.setName("Tournament 23");
        tournament.setPlayers(players);
        tournament.setTotalRounds(7);

        List<ChesspairingRound> rounds = new ArrayList<>();
        tournament.setRounds(rounds);
        return tournament;
    }

    /**
     * it uses buildTournament 23 and it sets the first round();
     *
     * @return
     */
    public static ChesspairingTournament rigFirstRoundOn23() {
        ChesspairingTournament tournament = buildTournament23();
        List<ChesspairingPlayer> pool = new ArrayList<>();
        List<ChesspairingGame> games = new ArrayList<>();
        pool.addAll(tournament.getPlayers());
        //<riged games>
        int tableNumber = 0;
        while (pool.size() >= 2) {
            ChesspairingGame game = new ChesspairingGame();
            game.setWhitePlayer(pool.remove(0));
            game.setBlackPlayer(pool.remove(pool.size() - 1));
            game.setTableNumber(++tableNumber);
            game.setResult(ChesspairingResult.WHITE_WINS);
            games.add(game);
        }
        if (pool.size() != 1) {
            throw new IllegalStateException("This should be 1");
        }
        //build the buy player
        ChesspairingGame game = new ChesspairingGame();
        game.setWhitePlayer(pool.remove(0));
        game.setTableNumber(++tableNumber);
        game.setResult(ChesspairingResult.BYE);
        games.add(game);

        ChesspairingRound round = new ChesspairingRound();
        round.setRoundNumber(1);
        round.setGames(games);
        round.setPresentPlayers(tournament.getPlayers());
        List emtyList = new ArrayList<>();
        round.setDownfloaters(emtyList);
        round.setUpfloaters(emtyList);

        List<ChesspairingRound> rounds = tournament.getRounds();
        rounds.add(round);
        return tournament;
    }

    public static List<ChesspairingPlayer> buildPlayers() {
        List<ChesspairingPlayer> players = new ArrayList<ChesspairingPlayer>();
        players.add(buildPlayer("Bogdan Marian", 1450));
        players.add(buildPlayer("Lacramioara", 1320));
        players.add(buildPlayer("Calin Constantin", 1550));
        players.add(buildPlayer("Cezar Andrei", 1200));
        players.add(buildPlayer("Rares", 1600));
        players.add(buildPlayer("Catalin Ioan", 1750));
        players.add(buildPlayer("Maria", 1700));
        players.add(buildPlayer("Delia Ioana", 1750));
        return players;
    }

    public static ChesspairingPlayer buildPlayer(String playerName, int elo) {
        ChesspairingPlayer player = new ChesspairingPlayer();
        player.setName(playerName);
        player.setElo(elo);
        player.setPlayerKey(String.valueOf(elo) + playerName);
        return player;
    }

    /**
     * Useful tool in debugging when sometimes you just want to look at a
     * tournament in clear text instead of the debugging console
     *
     * @param tournament is the tournament that will be serialized to file
     * @param fileName   is where the tournament will be serialized
     */
    public static void writeToFile(ChesspairingTournament tournament, String fileName) {
        createIfNotPresentGeneratedFilesFolder();
        String filePath = Tools.GENERATED_FILES + "/" + fileName;
        try {
            Writer writer = new FileWriter(filePath);
            Gson gson = Tools.getGson();
            gson.toJson(tournament, writer);
            writer.close();
        } catch (IOException e) {
            throw new IllegalStateException("Not able to write to file: " + filePath + "\n" + e.getMessage());
        }

    }

    public static ChesspairingTournament loadFile(String fileProjectPath) {
        InputStream inputStream = TestUtils.class.getResourceAsStream(fileProjectPath);
        Reader reader;
        try {
            reader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e.getMessage());
        }
        Gson gson = Tools.getGson();
        ChesspairingTournament tournament = gson.fromJson(reader, ChesspairingTournament.class);
        return tournament;
    }

    public static ChesspairingGame buildGame(int tableNumber,
                                             ChesspairingPlayer whitePlayer,
                                             ChesspairingPlayer blackPlayer,
                                             ChesspairingResult result) {
        ChesspairingGame game = new ChesspairingGame(tableNumber, whitePlayer, blackPlayer);
        game.setResult(result);
        return game;
    }
}
