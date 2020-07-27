package eu.chessdata.chesspairing.importexport;

import com.fasterxml.jackson.core.JsonProcessingException;
import eu.chessdata.chesspairing.model.ChesspairingTournament;

import java.io.IOException;
import java.io.InputStream;

public interface ImportExportTool {
    ChesspairingTournament buildFromStream(InputStream sourceStream) throws IOException;

    ChesspairingTournament buildFromString(String sourceString) throws JsonProcessingException;
}
