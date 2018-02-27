package eu.chessdata.chesspairing.model;

import java.security.cert.CertStoreSpi;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.chessdata.chesspairing.algoritms.comparators.ByEloReverce;
import eu.chessdata.chesspairing.algoritms.comparators.ByInitialOrderIdReverce;
import eu.chessdata.chesspairing.algoritms.fideswissduch.v2.Game;
import eu.chessdata.chesspairing.algoritms.fideswissduch.v2.Player;

public class ChesspairingTournament {
	private String name;
	private String description;
	private String city;
	private String federation;
	private Date dateOfStart;
	private Date dateOfEnd;
	private String typeOfTournament;
	private String ChifArbiter;
	private String deputyChifArbiters;
	/**
	 * this is the maximum allowed number of rounds in a tournament. If you try to
	 * pare over this number some algorithms will just crash.
	 */
	private int totalRounds;
	private ChesspairingByeValue chesspairingByeValue;
	private List<ChesspairingPlayer> players = new ArrayList<ChesspairingPlayer>();
	private List<ChesspairingRound> rounds = new ArrayList<ChesspairingRound>();
	private PairingSummary parringSummary;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getFederation() {
		return federation;
	}

	public void setFederation(String federation) {
		this.federation = federation;
	}

	public Date getDateOfStart() {
		return dateOfStart;
	}

	public void setDateOfStart(Date dateOfStart) {
		this.dateOfStart = dateOfStart;
	}

	public Date getDateOfEnd() {
		return dateOfEnd;
	}

	public void setDateOfEnd(Date dateOfEnd) {
		this.dateOfEnd = dateOfEnd;
	}

	public String getTypeOfTournament() {
		return typeOfTournament;
	}

	public void setTypeOfTournament(String typeOfTournament) {
		this.typeOfTournament = typeOfTournament;
	}

	public String getChifArbiter() {
		return ChifArbiter;
	}

	public void setChifArbiter(String chifArbiter) {
		ChifArbiter = chifArbiter;
	}

	public String getDeputyChifArbiters() {
		return deputyChifArbiters;
	}

	public void setDeputyChifArbiters(String deputyChifArbiters) {
		this.deputyChifArbiters = deputyChifArbiters;
	}

	public ChesspairingByeValue getChesspairingByeValue() {
		if (null == this.chesspairingByeValue) {
			this.chesspairingByeValue = ChesspairingByeValue.ONE_POINT;
		}
		return chesspairingByeValue;
	}

	public void setChesspairingByeValue(ChesspairingByeValue chesspairingByeValue) {
		this.chesspairingByeValue = chesspairingByeValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTotalRounds() {
		return totalRounds;
	}

	public void setTotalRounds(int totalRounds) {
		this.totalRounds = totalRounds;
	}

	public List<ChesspairingRound> getRounds() {
		return rounds;
	}

	public void setRounds(List<ChesspairingRound> rounds) {
		this.rounds = rounds;
	}

	public List<ChesspairingPlayer> getPlayers() {
		return players;
	}

	public void setPlayers(List<ChesspairingPlayer> players) {
		this.players = players;
	}

	public PairingSummary getParringSummary() {
		return parringSummary;
	}

	public void setParringSummary(PairingSummary parringSummary) {
		this.parringSummary = parringSummary;
	}

	/**
	 * It returns the player in {@link #players} by player key
	 * 
	 * @param playerKey
	 *            is the key of a specific player
	 * @return a player and throws exception if player does not exist
	 */
	public ChesspairingPlayer getPlayer(String playerKey) {
		for (ChesspairingPlayer player : players) {
			String key = player.getPlayerKey();
			if (key.equals(playerKey)) {
				return player;
			}
		}
		throw new IllegalStateException("Player does not exist in the players list");
	}

	/**
	 * It returns the player by initial index If index is 0 or does not exist in the
	 * tournament ten it throws exception
	 * 
	 * @param indexPlayer
	 *            is the index of the player
	 * @return the playerf located in the players list
	 */
	public ChesspairingPlayer getPlayerByInitialRank(int indexPlayer) {
		if (indexPlayer <= 0) {
			throw new IllegalStateException("Index is <= then zero");
		}
		if (indexPlayer > players.size()) {
			throw new IllegalStateException("Index is >= players.size()");
		}

		for (ChesspairingPlayer player : this.players) {
			if (player.getInitialOrderId() == indexPlayer) {
				return player;
			}
		}

		throw new IllegalStateException("Index nod found. indexPlayer = " + indexPlayer);
	}

	/**
	 * It adds a round to this tournament;
	 * 
	 * @param round
	 */
	public void addRound(ChesspairingRound round) {
		this.rounds.add(round);
	}

	/**
	 * Compute players ranking after all games are played for a specific round
	 * 
	 * @param roundNumber
	 *            is the round number for witch wee have to compute the standings
	 * @return and ordered list of players. The best player is ranked number one
	 */
	public List<ChesspairingPlayer> computeStandings(int roundNumber) {
		List<ChesspairingPlayer> standings = new ArrayList<>();
		standings.addAll(this.players);
		
		final Map<ChesspairingPlayer, Float> pointsMap = new HashMap<>();
		//set the points to 0
		for (ChesspairingPlayer player: standings) {
			pointsMap.put(player, 0f);
		}
		
		for (int i = 1; i <= roundNumber; i++) {
			ChesspairingRound round = getRoundByRoundNumber(i);
			if (!round.allGamesHaveBeanPlayed()) {
				throw new IllegalStateException("Atempt to compute standings when there are still games with no result");
			} 
			
			//TODO cycle all games and collect the points
			for (ChesspairingPlayer player: standings) {
				Float points = round.getPointsFor(player, this.getChesspairingByeValue());
				Float initialPoints = pointsMap.get(player);
				Float result = points + initialPoints;
				pointsMap.put(player, result);
			}
		}
		
		//collect all games. for each player create a list with all the games that he played
		final Map<ChesspairingPlayer, List<ChesspairingGame>> playerGames = new HashMap<>();
		for (ChesspairingPlayer player: this.players) {
			List<ChesspairingGame>games = new ArrayList<>();
			playerGames.put(player, games);
		}
		for (int i=1;i<=roundNumber;i++) {
			//for each 
		}
		
		Comparator<ChesspairingPlayer> byPoints = new Comparator<ChesspairingPlayer>() {
			
			@Override
			public int compare(ChesspairingPlayer o1, ChesspairingPlayer o2) {
				// TODO Auto-generated method stub
				Float points1 = pointsMap.get(o1);
				Float points2 = pointsMap.get(o2);
				//compare in reverce order
				
				return points2.compareTo(points1);
			}
		};
		
		Comparator<ChesspairingPlayer> byDirectMatches = new Comparator<ChesspairingPlayer>() {

			@Override
			public int compare(ChesspairingPlayer o1, ChesspairingPlayer o2) {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		
		Comparator<ChesspairingPlayer> revercedInitialOrder = new ByInitialOrderIdReverce();
		
		throw new IllegalStateException("Please implement compute standings");
	}

	/**
	 * It finds the round by a specific round number. If the round requested does
	 * not exist then the request it will just throw exception
	 * 
	 * @param roundNumber
	 *            of the round requested
	 * @return the round identified by round number
	 */
	public ChesspairingRound getRoundByRoundNumber(int roundNumber) {
		for (ChesspairingRound round : getRounds()) {
			if (roundNumber == round.getRoundNumber()) {
				return round;
			}
		}

		// no round located
		throw new IllegalStateException("Not able to locate round nr " + roundNumber);
	}

}
