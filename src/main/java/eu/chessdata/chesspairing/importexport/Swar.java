package eu.chessdata.chesspairing.importexport;

import eu.chessdata.chesspairing.model.ChesspairingTournament;

import java.io.InputStream;

public class Swar implements ImportExportTool {

    @Override
    public ChesspairingTournament buildFromStream(InputStream sourceStream) {
        throw new IllegalStateException("Please implement this");
    }
}
