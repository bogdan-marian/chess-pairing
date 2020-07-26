package eu.chessdata.chesspairing.importexport;

import eu.chessdata.chesspairing.model.ChesspairingTournament;

import java.io.IOException;
import java.io.InputStream;

public interface ImportExportTool {
    ChesspairingTournament buildFromStream(InputStream sourceStream) throws IOException;
}
