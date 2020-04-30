package eu.chessdata.chesspairing.algoritms.javafo;

import eu.chessdata.chesspairing.algoritms.fideswissduch.Algorithm;
import eu.chessdata.chesspairing.model.ChesspairingGame;
import eu.chessdata.chesspairing.model.ChesspairingPlayer;
import eu.chessdata.chesspairing.model.ChesspairingRound;
import eu.chessdata.chesspairing.model.ChesspairingTournament;
import eu.chessdata.chesspairing.tools.Trf;
import javafo.api.JaVaFoApi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JavafoWrapp implements Algorithm {

	@Override
	public ChesspairingTournament generateNextRound(ChesspairingTournament tournament) {
		// //compute next round number
		// int nextRoundNumber = tournament.getRounds().size() + 1;

		String trf = Trf.getTrf(tournament);
		System.out.println(trf);
		InputStream inputStream = new ByteArrayInputStream(trf.getBytes());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JaVaFoApi.exec(1000, "Hello World", inputStream, outputStream);
		String javafoResult = outputStream.toString();
		String newline = System.getProperty("line.separator");
		String[] pares = javafoResult.split(newline);

		/**
		 * Decode the result: first item is number of pares and next lines are
		 * the number of pares
		 */
		List<ChesspairingGame> games = new ArrayList<>();

		int totalPares = Integer.valueOf(pares[0]);
		for (int i = 1; i <= totalPares; i++) {
			String pare[] = pares[i].split(" ");
			int indexWhite = Integer.valueOf(pare[0]);
			int indexBlack = Integer.valueOf(pare[1]);
			int tableNumber = i;

			if (indexBlack == 0) {
				ChesspairingPlayer player = tournament.getPlayerByInitialRank(indexWhite);
				ChesspairingGame buyGame = ChesspairingGame.buildBuyGame(tableNumber, player);

				games.add(buyGame);
			} else {

				ChesspairingPlayer whitePlayer = tournament.getPlayerByInitialRank(indexWhite);
				ChesspairingPlayer blackPlayer = tournament.getPlayerByInitialRank(indexBlack);

				ChesspairingGame game = new ChesspairingGame(tableNumber, whitePlayer, blackPlayer);
				games.add(game);
			}
		}

		// compute to what round wee need to add the games

		if (tournament.getRounds().size() == 0) {
			int nextRoundNumber = 1;
			ChesspairingRound round = ChesspairingRound.buildRound(nextRoundNumber, games);
			tournament.addRound(round);
		} else {
			int lastRoundNumber = tournament.getRounds().size();
			if (tournament.getRounds().get(lastRoundNumber - 1).hasGames()) {
				// last round already has games
				int nextRoundNumber = lastRoundNumber + 1;
				ChesspairingRound round = ChesspairingRound.buildRound(nextRoundNumber, games);
				tournament.addRound(round);
			} else {
				// last round does not have games
				ChesspairingRound round = tournament.getRounds().get(lastRoundNumber - 1);
				round.setGames(games);
			}
		}
		return tournament;
	}

}
