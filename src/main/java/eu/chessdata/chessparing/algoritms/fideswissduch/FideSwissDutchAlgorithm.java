package eu.chessdata.chessparing.algoritms.fideswissduch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.chessdata.chessparing.Tools;
import eu.chessdata.chessparing.algoritms.comparators.ByElo;
import eu.chessdata.chessparing.model.ChessparingGame;
import eu.chessdata.chessparing.model.ChessparingPlayer;
import eu.chessdata.chessparing.model.ChessparingRound;
import eu.chessdata.chessparing.model.ChessparingTournament;
import eu.chessdata.chessparing.model.ParringSummary;

public class FideSwissDutchAlgorithm implements Algorithm {
	private ChessparingTournament mTournament;

	public ChessparingTournament generateNextRound(ChessparingTournament tournament) {
		this.mTournament = tournament;
		this.mTournament.setParringSummary(Tools.buildParringStarted());
		// more tan 1 players
		if (mTournament.getPlayers().size() < 2) {
			throw new IllegalStateException("Please ad at least 2 players or more");
		}

		// more rounds than totalRounds? For the moment I do not want to deal
		// with this use case
		if (mTournament.getTotalRounds() <= mTournament.getRounds().size()) {
			throw new IllegalStateException("You are trying to generate more rounds than totalRounds");
		}
		
		boolean validationOk = validateOrder();
		if (!validationOk){
			return mTournament;
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
		
		ParringSummary firstRoundOk = new ParringSummary();
		firstRoundOk.setShortMessage(ParringSummary.PARRING_OK);
		firstRoundOk.setLongMessage("First round was generated");
		mTournament.setParringSummary(firstRoundOk);
	}

	/**
	 * if players with no initialOrderId or the same initialOrderId then do not pare
	 * and set the paring message accordingly. No initial order is considered smaller or equal to 0
	 * @return  true if validation is OK and false otherwise
	 */
	private boolean validateOrder(){
		//players with no id
		List<String> playersNoId = new ArrayList<>();
		List<ChessparingPlayer> players = mTournament.getPlayers();
		for (ChessparingPlayer player: players){
			if (player.getInitialOrderId()<=0){
				playersNoId.add(player.getName());
			}
		}
		if (playersNoId.size()>0){
			StringBuffer sb = new StringBuffer();
			sb.append("You have players with no initialOrderId: ");
			for(String name: playersNoId){
				sb.append(name+", ");//
			}
			int id = sb.lastIndexOf(", ");
			sb.replace(id, sb.length(), "");
			ParringSummary parringSummary = new ParringSummary();
			parringSummary.setShortMessage(ParringSummary.PARRING_NOT_OK);
			parringSummary.setLongMessage(sb.toString());
			mTournament.setParringSummary(parringSummary);
			return false;
		}
		
		//players with the same id
		Map<Integer,ChessparingPlayer> map = new HashMap<>();
		for (ChessparingPlayer player: players){
			if(map.containsKey(player.getInitialOrderId())){
				StringBuffer sb = new StringBuffer();
				sb.append("You have players with the same initialOrderId: ");
				sb.append(map.get(player.getInitialOrderId()).getName()+" and ");
				sb.append(player.getName());
				ParringSummary parringSummary = new ParringSummary();
				parringSummary.setShortMessage(ParringSummary.PARRING_NOT_OK);
				parringSummary.setLongMessage(sb.toString());
				mTournament.setParringSummary(parringSummary);
				return false;
			}else{
				map.put(player.getInitialOrderId(), player);
			}
		}
		return true;
	}
}
