package eu.chessdata.chesspairing.algoritms.comparators;

import eu.chessdata.chesspairing.model.ChesspairingPlayer;
import eu.chessdata.chesspairing.model.ChesspairingTournament;

import java.util.Comparator;

public class ByBuchholzPointsReverse implements Comparator<ChesspairingPlayer> {

    private int roundNumber;
    private ChesspairingTournament tournament;

    public ByBuchholzPointsReverse(int roundNumber, ChesspairingTournament tournament) {
        this.roundNumber = roundNumber;
        this.tournament = tournament;
    }

    @Override
    public int compare(ChesspairingPlayer player, ChesspairingPlayer toPlayer) {
        float points = tournament.computeBuchholzPoints(roundNumber, player.getPlayerKey());
        float toPoints = tournament.computeBuchholzPoints(roundNumber, toPlayer.getPlayerKey());
        return -1 * Float.compare(points, toPoints);
    }
}
