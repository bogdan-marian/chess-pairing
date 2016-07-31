package eu.chessdata.chessparing.algoritms.comparators;

import java.util.Comparator;

import eu.chessdata.chessparing.model.ChessparingPlayer;

/**
 * It sorts in the descending order the players by elo
 * @author bogda
 *
 */
public class ByElo implements Comparator<ChessparingPlayer>{

	@Override
	public int compare(ChessparingPlayer o1, ChessparingPlayer o2) {
		if (o1 == null){
			throw new IllegalStateException("Player o1 is null");
		}
		if (o2 == null){
			throw new IllegalStateException("Player o2 is null");
		}
		
		int elo1 = o1.getElo();
		int elo2 = o2.getElo();
		//just multiply by -1 the default Integer compare
		return -1* Integer.compare(elo1, elo2);
	}
}
