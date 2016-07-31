package eu.chessdata.chessparing.algoritms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.chessdata.chessparing.algoritms.comparators.ByElo;
import eu.chessdata.chessparing.model.ChessparingGame;
import eu.chessdata.chessparing.model.ChessparingPlayer;
import eu.chessdata.chessparing.model.ChessparingRound;
import eu.chessdata.chessparing.model.ChessparingTournament;

public class FideSwissDutchAlgorithm implements Algorithm {
	private ChessparingTournament mTournament;

	public ChessparingTournament generateNextRound(ChessparingTournament tournament) {
		this.mTournament = tournament;
		// more tan 1 players
		if (mTournament.getPlayers().size() < 2) {
			throw new IllegalStateException("Please ad at least 2 players or more");
		}

		List<ChessparingRound> rounds = this.mTournament.getRounds();
		if (rounds.size() <= 0) {
			generateFirstRound();
			return this.mTournament;
		}
		throw new UnsupportedOperationException("Please implement this");
	}

	/**
	 * For the moment I will sort the players in the descending order by elo and
	 * group them 2 by 2 I'm still wondering if I should group the first with
	 * the last and so on.
	 */
	private void generateFirstRound() {
		if (this.mTournament.getRounds().size() > 0) {
			throw new IllegalStateException("Tournament allready contains round 1");
		}

		Collections.sort(mTournament.getPlayers(), new ByElo());
		List<ChessparingGame> games = new ArrayList<>();
		List<ChessparingPlayer> players = mTournament.getPlayers();
		int count = 0;
		ChessparingGame game = new ChessparingGame();
		game.setTableNumber(0);
		for (ChessparingPlayer player : players) {
			count++;
			if (count % 2 == 1) {
				int tableNumber = game.getTableNumber() + 1;
				game = new ChessparingGame();
				game.setTableNumber(tableNumber);
				game.setWhitePlayer(player);
				if (count == players.size()) {
					game.setResult(ChessparingGame.RESULT_WITE_WINS);
					games.add(game);
				}
			} else {
				game.setBlackPlayer(player);
				games.add(game);
			}
		}
		ChessparingRound round = new ChessparingRound();
		round.setRoundNumber(1);
		round.setGames(games);

		// is the first round so wee can create all new data
		List<ChessparingRound> rounds = new ArrayList<>();

		// add the round
		rounds.add(round);

		// and wee set the rounds
		mTournament.setRounds(rounds);
	}
}
