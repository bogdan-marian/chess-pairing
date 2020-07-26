package eu.chessdata.chesspairing.importexport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.chessdata.chesspairing.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Swar implements ImportExportTool {

    private ObjectMapper mapper = new ObjectMapper();
    private Map<String, ChesspairingPlayer> niMap = new HashMap<>();


    @Override
    public ChesspairingTournament buildFromStream(InputStream sourceStream) throws IOException {
        ChesspairingTournament tournament = new ChesspairingTournament();

        JsonNode jsonNode = mapper.readTree(sourceStream);
        JsonNode swarNode = jsonNode.get("Swar");
        JsonNode tournamentDescription = swarNode.get("TournamentDesc");
        String tournamentName = tournamentDescription.get("Tournament").asText();
        tournament.setName(tournamentName);
        tournament.setPlayers(decodeTournamentPlayers(swarNode));

        int totalRound = tournamentDescription.get("NbOfRounds").asInt();
        tournament.setTotalRounds(totalRound);

        tournament.setRounds(decodeTournamentRounds(swarNode));

        return tournament;
    }


    private List<ChesspairingPlayer> decodeTournamentPlayers(JsonNode swarNode) {
        List<ChesspairingPlayer> players = new ArrayList<>();

        JsonNode jsonPlayers = swarNode.get("Player");
        for (JsonNode playerNode : jsonPlayers) {
            String name = playerNode.get("Name").asText();
            String id = playerNode.get("NationalId").asText();
            ChesspairingPlayer player = new ChesspairingPlayer();
            player.setName(name);
            player.setPlayerKey(id);
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
                    throw new IllegalStateException("Please process also bye");
                }
                Integer tableNumber = Integer.valueOf(tableId);

                if (!round.hasGameForTable(tableNumber)) {
                    // build the game
                    ChesspairingGame game = new ChesspairingGame();
                    game.setTableNumber(tableNumber);
                    round.getGames().add(game);

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

}
