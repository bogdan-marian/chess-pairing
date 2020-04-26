package eu.chessdata.chesspairing.algoritms.comparators;

import eu.chessdata.chesspairing.model.ChesspairingPlayer;

import java.util.Comparator;

public class ByInitialOrderIdReverse implements Comparator<ChesspairingPlayer> {

	@Override
	public int compare(ChesspairingPlayer o1, ChesspairingPlayer o2) {
		if (o1 == null) {
			throw new IllegalStateException("Player o1 is null");
		}
		if (o2 == null) {
			throw new IllegalStateException("Player o2 is null");
		}
		// use the default integer compare for help
		return -1 * Integer.compare(o1.getInitialOrderId(), o2.getInitialOrderId());
	}
}
