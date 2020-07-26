package eu.chessdata.chesspairing.importexport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.chessdata.chesspairing.model.ChesspairingPlayer;
import eu.chessdata.chesspairing.model.ChesspairingRound;
import eu.chessdata.chesspairing.model.ChesspairingTournament;

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
        List<ChesspairingRound> rounds = new ArrayList<>();

        JsonNode jsonPlayers = swarNode.get("Player");
        for (JsonNode playerNode : jsonPlayers) {
            /**
             * Swar specific id that specifies after what round this tournament has bean exported
             */
            int nbRound = playerNode.get("NbRounds").asInt();
            if (nbRound < 1) {
                return rounds;
            }
        }

        return rounds;
    }

}
