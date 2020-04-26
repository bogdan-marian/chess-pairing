package eu.chessdata.chesspairing.algoritms.comparators;

import eu.chessdata.chesspairing.model.ChesspairingPlayer;

import java.util.Comparator;

/**
 * It sorts in the descending order the players by elo
 *
 * @author bogdan
 */
public class ByEloReverse implements Comparator<ChesspairingPlayer> {

	@Override
	public int compare(ChesspairingPlayer o1, ChesspairingPlayer o2) {
		if (o1 == null) {
			throw new IllegalStateException("Player o1 is null");
		}
		if (o2 == null) {
			throw new IllegalStateException("Player o2 is null");
		}
		
		int elo1 = o1.getElo();
		int elo2 = o2.getElo();
		//just multiply by -1 the default Integer compare
		return -1* Integer.compare(elo1, elo2);
	}
}
