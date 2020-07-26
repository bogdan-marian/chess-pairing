package eu.chessdata.chesspairing.importexport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.chessdata.chesspairing.model.ChesspairingPlayer;
import eu.chessdata.chesspairing.model.ChesspairingTournament;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Swar implements ImportExportTool {

    private ObjectMapper mapper = new ObjectMapper();


    @Override
    public ChesspairingTournament buildFromStream(InputStream sourceStream) throws IOException {
        ChesspairingTournament tournament = new ChesspairingTournament();

        JsonNode jsonNode = mapper.readTree(sourceStream);
        JsonNode swarNode = jsonNode.get("Swar");
        JsonNode tournamentDescription = swarNode.get("TournamentDesc");
        String tournamentName = tournamentDescription.get("Tournament").asText();
        tournament.setName(tournamentName);
        tournament.setPlayers(decodeTournamentPlayers(swarNode));
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
        }
        return players;
    }
}
