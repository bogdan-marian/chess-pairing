package eu.chessdata.chesspairing.importexport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.chessdata.chesspairing.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Swar implementation of the {@link ImportExportTool}
 * <p>
 * Rank is initial ranking
 * Ranking is rank at specific tournament export time
 */
public class Swar implements ImportExportTool {

    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, ChesspairingPlayer> niMap = new HashMap<>();
    public String idField;

    private Swar(String filedUsedAsId) {
        this.idField = filedUsedAsId;
    }

    public static Swar newInstance(String fieldUsedAsId) {
        Swar swar = new Swar(fieldUsedAsId);
        return swar;
    }


    @Override
    public String getFieldUsedAsId() {
        return idField;
    }

    @Override
    public ChesspairingTournament buildFromString(String sourceString) throws JsonProcessingException {
        JsonNode jsonNode = mapper.readTree(sourceString);
        return decodeSwarTournament(jsonNode);
    }

    @Override
    public ChesspairingTournament buildFromStream(InputStream sourceStream) throws IOException {
        JsonNode jsonNode = mapper.readTree(sourceStream);
        return decodeSwarTournament(jsonNode);
    }

    private ChesspairingTournament decodeSwarTournament(JsonNode jsonNode) {
        if (null == idField) {
            throw new IllegalStateException("Swar fieldId not initialized");
        }

        ChesspairingTournament tournament = new ChesspairingTournament();
        JsonNode swarNode = jsonNode.get("Swar");
        JsonNode tournamentDescription = swarNode.get("TournamentDesc");
        String tournamentName = tournamentDescription.get("Tournament").asText();
        tournament.setName(tournamentName);
        tournament.setPlayers(decodeTournamentPlayers(swarNode));

        int totalRound = tournamentDescription.get("NbOfRounds").asInt();
        tournament.setTotalRounds(totalRound);

        tournament.setRounds(decodeTournamentRounds(swarNode));

        tournament.setStandings(decodeStandings(swarNode));

        List<ChesspairingPlayer> orderedPlayers = orderPlayers(tournament.getPlayers());
        tournament.setPlayers(orderedPlayers);

        return tournament;
    }

    private List<ChesspairingPlayer> orderPlayers(List<ChesspairingPlayer> players) {
        List<ChesspairingPlayer> copyList = new ArrayList<>();
        copyList.addAll(players);
        Collections.sort(copyList, new Comparator<ChesspairingPlayer>() {
            @Override
            public int compare(ChesspairingPlayer p0, ChesspairingPlayer p1) {
                return (p0.getRank() - p1.getRank());
            }
        });
        return copyList;
    }

    private List<ChesspairingPlayer> decodeTournamentPlayers(JsonNode swarNode) {
        List<ChesspairingPlayer> players = new ArrayList<>();

        JsonNode jsonPlayers = swarNode.get("Player");
        for (JsonNode playerNode : jsonPlayers) {
            String name = playerNode.get("Name").asText();
            String id = playerNode.get(idField).asText();
            int rank = playerNode.get("Ranking").asInt();
            int initialRank = playerNode.get("Rank").asInt();

            ChesspairingPlayer player = new ChesspairingPlayer();
            player.setName(name);
            player.setPlayerKey(id);
            player.setRank(rank);
            player.setInitialOrderId(initialRank);

            players.add(player);

            String ni = playerNode.get("Ni").asText();
            niMap.put(ni, player);
        }
        return players;
    }

    private List<ChesspairingRound> decodeTournamentRounds(JsonNode swarNode) {

        Map<Integer, ChesspairingRound> mapRounds = new HashMap<>();
        JsonNode jsonPlayers = swarNode.get("Player");
        for (JsonNode playerNode : jsonPlayers) {
            /**
             * Swar specific id that specifies after what round this tournament has bean exported
             */
            int nbRound = playerNode.get("NbRounds").asInt();
            if (nbRound < 1) {
                return new ArrayList<>();
            }
            String ni = playerNode.get("Ni").asText();
            ChesspairingPlayer playerThis = niMap.get(ni);

            JsonNode jsonRoundArray = playerNode.get("RoundArray");

            for (JsonNode jsonGame : jsonRoundArray) {
                Integer roundId = jsonGame.get("RoundNr").asInt();
                if (!mapRounds.containsKey(roundId)) {
                    ChesspairingRound round = new ChesspairingRound();
                    round.setRoundNumber(roundId);
                    mapRounds.put(roundId, round);
                }
                ChesspairingRound round = mapRounds.get(roundId);

                String tableId = jsonGame.get("Tabel").asText();
                if (tableId.equals("BYE")) {
                    ChesspairingGame byeGame = new ChesspairingGame();
                    round.getGames().add(byeGame);

                    byeGame.setTableNumber(0);
                    byeGame.setWhitePlayer(playerThis);
                    byeGame.setResult(ChesspairingResult.BYE);
                    continue;

                }
                if (tableId.equals("Absent")) {
                    List<ChesspairingPlayer> absentPlayers = round.getAbsentPlayers();
                    absentPlayers.add(playerThis);
                    continue;
                }
                Integer tableNumber = Integer.valueOf(tableId);

                if (!round.hasGameForTable(tableNumber)) {
                    // build the game
                    ChesspairingGame game = new ChesspairingGame();
                    round.getGames().add(game);

                    game.setTableNumber(tableNumber);

                    String niThat = jsonGame.get("OpponentNi").asText();
                    ChesspairingPlayer playerThat = niMap.get(niThat);

                    // color
                    String color = jsonGame.get("Color").asText();
                    if (color.equals("White")) {
                        game.setWhitePlayer(playerThis);
                        game.setBlackPlayer(playerThat);
                    }

                    if (color.equals("Black")) {
                        game.setWhitePlayer(playerThat);
                        game.setBlackPlayer(playerThis);
                    }

                    // result
                    String result = jsonGame.get("Result").asText();
                    if (result.equals("-")) {
                        game.setResult(ChesspairingResult.NOT_DECIDED);
                    }

                    if (result.equals("0")) {
                        if (color.equals("White")) {
                            game.setResult(ChesspairingResult.BLACK_WINS);
                        }
                        if (color.equals("Black")) {
                            game.setResult(ChesspairingResult.WHITE_WINS);
                        }
                    }

                    if (result.equals("1")) {
                        if (color.equals("White")) {
                            game.setResult(ChesspairingResult.WHITE_WINS);
                        }
                        if (color.equals("Black")) {
                            game.setResult(ChesspairingResult.BLACK_WINS);
                        }
                    }

                    if (result.equals("½")) {
                        game.setResult(ChesspairingResult.DRAW_GAME);
                    }

                }
            }

        }

        List<ChesspairingRound> rounds = new ArrayList<>(mapRounds.values());
        return rounds;
    }

    private List<ChesspairingStanding> decodeStandings(JsonNode swarNode) {
        Map<Integer, ChesspairingRound> mapRounds = new HashMap<>();
        JsonNode jsonPlayers = swarNode.get("Player");

        List<ChesspairingStanding> standings = new ArrayList<>();

        for (JsonNode playerNode : jsonPlayers) {

            ChesspairingStanding standing = new ChesspairingStanding();
            standings.add(standing);

            String ni = playerNode.get("Ni").asText();
            ChesspairingPlayer playerThis = niMap.get(ni);
            standing.setPlayer(playerThis);

            int rank = playerNode.get("Ranking").asInt();
            standing.setRank(rank);

            float pointsAtRound = playerNode.get("Points").asLong();
            standing.setPoints(pointsAtRound);

            JsonNode tieBreakNode = playerNode.get("TieBreak");

            for (JsonNode tieItemNode : tieBreakNode) {

                String type = tieItemNode.get("Type").asText();
                String points = tieItemNode.get("Points").asText();
                switch (type) {
                    case "Direct Encounter":
                        if (points.equals("-")) {
                            standing.setDirectEncounter(0f);
                        } else {
                            standing.setDirectEncounter(Float.valueOf(points));
                        }
                        break;
                    case "Nb.Games Won":
                        standing.setGamesWon(Integer.valueOf(points));
                        break;
                    case "Nb.played with Black":
                        standing.setGamesPlayedWithBlack(Integer.valueOf(points));
                        break;
                    case "Average Rating Opp.":
                        standing.setAverageRatingOpp(Float.valueOf(points));
                        break;
                    case "Bucholtz Cut 1":
                        standing.setBucholtzCut1(Float.valueOf(points));
                        break;
                }
            }

        }
        return standings;
    }

}
