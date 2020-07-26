package eu.chessdata.chesspairing.importexport;


import com.fasterxml.jackson.databind.ObjectMapper;
import eu.chessdata.chesspairing.model.ChesspairingPlayer;
import eu.chessdata.chesspairing.model.ChesspairingTournament;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class SwarTest {

    @Test
    public void loadExport01NoPairingsTest() throws IOException {
        InputStream inputStream = SwarTest.class.getResourceAsStream(
                "/importexport/swar/Swar-export-01-no-parings.json");

        ObjectMapper mapper = new ObjectMapper();
        Swar swar = new Swar();
        ChesspairingTournament tournament = swar.buildFromStream(inputStream);
        Assert.assertTrue(tournament.getName().equals("Chessout-export"));
        Optional<ChesspairingPlayer> optionalBogdan = tournament.getPlayerById("10001");
        Assert.assertTrue(optionalBogdan.isPresent());
        Assert.assertTrue(optionalBogdan.get().getName().equals("Bogdan"));
    }
}