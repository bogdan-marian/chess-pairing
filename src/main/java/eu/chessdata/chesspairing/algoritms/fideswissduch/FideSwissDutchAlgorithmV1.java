package eu.chessdata.chesspairing.algoritms.fideswissduch;

import eu.chessdata.chesspairing.algoritms.comparators.ByElo;
import eu.chessdata.chesspairing.algoritms.comparators.ByEloReverse;
import eu.chessdata.chesspairing.algoritms.comparators.ByInitialOrderIdReverse;
import eu.chessdata.chesspairing.model.*;
import eu.chessdata.chesspairing.tools.Tools;

import java.util.*;
import java.util.Map.Entry;

public class FideSwissDutchAlgorithmV1 implements Algorithm {
    protected ChesspairingTournament mTournament;

    /**
     * groups of players The key
     */
    protected List<String> presentPlayerKeys;
    protected Map<String, Double> currentPoints;
    protected Map<Double, Map<String, ChesspairingPlayer>> groupsByResult;
	protected List<Double> orderedGroupKeys;
	// playerKey to color string
	protected Map<String, List<ChesspairingColour>> playerKeystoColorHistory;
	// playerKey to partners keys
	protected Map<String, List<String>> partnerHistory;
	// playerKey to upfloat counts
	protected Map<String, Integer> upfloatCounts;
	protected Map<String, Integer> downfloatCounts;
	protected Map<String, String> currentDownfloaters;
	protected ChesspairingGame buyGame;
	protected ChesspairingRound generatedRound;

	/**
	 * perform basic initializations and computations before the actual paring
	 * algorithm is started
	 */
	public ChesspairingTournament generateNextRound(ChesspairingTournament tournament) {
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
		if (!validationOk) {
			return mTournament;
		}

		List<ChesspairingRound> rounds = this.mTournament.getRounds();
		if (rounds.size() <= 0) {
			generateFirstRound();
			return this.mTournament;
		}

		// make sure that the next round can be generated
		if (!canIGenerateNextRound()) {
			mTournament.getParringSummary().setShortMessage(PairingSummary.PARRING_NOT_OK);
			mTournament.getParringSummary().setLongMessage("You can not generate the next round!");
			return mTournament;
		}

		int roundNumber = mTournament.getRounds().size();
		computeInitialTournamentState(roundNumber);

		computeNextRound(roundNumber + 1);
		// order games points,elo,index

		List<ChesspairingGame> games = this.generatedRound.getGames();
		// index
		// the smallest index first
		Collections.sort(games, new Comparator<ChesspairingGame>() {
			@Override
			public int compare(ChesspairingGame o1, ChesspairingGame o2) {
				int indexO1 = getHighestIndex(o1);
				int indexO2 = getHighestIndex(o2);
				return Integer.compare(indexO1, indexO2);
			}
		});

		// the highest elo first
		Collections.sort(games, new Comparator<ChesspairingGame>() {
			@Override
			public int compare(ChesspairingGame o1, ChesspairingGame o2) {
				int eloO1 = getHighestElo(o1);
				int eloO2 = getHighestElo(o2);
				// the highest value should be ordered first so wee multiply by
				// -1
				return -1 * Integer.compare(eloO1, eloO2);
			}
		});

		// the highest points first
		Collections.sort(games, new Comparator<ChesspairingGame>() {

			@Override
			public int compare(ChesspairingGame o1, ChesspairingGame o2) {
				Double pointsO1 = getHighestPoints(o1);
				Double pointsO2 = getHighestPoints(o2);
				return -1 * Double.compare(pointsO1, pointsO2);
			}
		});

		/**
		 * number the games
		 */
		int i = 1;
		for (ChesspairingGame game : games) {
			game.setTableNumber(i++);
		}
		//if buy game ad this game also to the games list
		if (this.buyGame != null){
			this.buyGame.setTableNumber(i);
			games.add(this.buyGame);
		}
		// add the generated round to the tournament
		this.setRound(this.generatedRound);
		return this.mTournament;
	}

	/**
	 * utility class that helps to set up vital properties required at next
	 * round computation
	 */
	protected void computeInitialTournamentState(int roundNumber) {
		computePresentPleyers();
		computeCurrentResults(roundNumber);
		computeCollorHistory(roundNumber);
		computePartnersHistory(roundNumber);
		computeUpfloatCounts(roundNumber);
		computeDlownFloatCounts(roundNumber);
	}

	private Double getHighestPoints(ChesspairingGame game) {
		if (game.getResult() == ChesspairingResult.BYE) {
			throw new IllegalStateException("It makes no sence to use this for a buy game");
		}
		ChesspairingPlayer player1 = game.getWhitePlayer();
		ChesspairingPlayer player2 = game.getBlackPlayer();
		Double pointsA = this.currentPoints.get(player1.getPlayerKey());
		Double pointsB = this.currentPoints.get(player2.getPlayerKey());
		if (pointsA < pointsB) {
			return pointsB;
		} else {
			return pointsA;
		}
	}

	private int getHighestIndex(ChesspairingGame game) {
		if (game.getResult() == ChesspairingResult.BYE) {
			throw new IllegalStateException("It makes no sence to use this for a buy game");
		}
		ChesspairingPlayer player1 = game.getWhitePlayer();
		ChesspairingPlayer player2 = game.getBlackPlayer();
		return getHighestNumber(player1.getInitialOrderId(), player2.getInitialOrderId());
	}

	private int getHighestElo(ChesspairingGame game) {
		if (game.getResult() == ChesspairingResult.BYE) {
			throw new IllegalStateException("It makes no sence to use this for a buy game");
		}
		ChesspairingPlayer player1 = game.getWhitePlayer();
		ChesspairingPlayer player2 = game.getBlackPlayer();
		return getHighestNumber(player1.getElo(), player2.getElo());
	}

	private int getHighestNumber(int a, int b) {
		if (a > b) {
			return a;
		} else {
			return b;
		}
	}

	/**
	 * compute upfloat counts for the previous rounds
	 * 
	 * @param roundNumber
	 */
	protected void computeUpfloatCounts(int roundNumber) {
		this.upfloatCounts = new HashMap<>();
		for (String playerKey : this.presentPlayerKeys) {
			this.upfloatCounts.put(playerKey, new Integer(0));
		}

		for (int i = 1; i < roundNumber; i++) {
			for (ChesspairingPlayer upfloater : getRound(i).getUpfloaters()) {
				Integer count = this.upfloatCounts.get(upfloater.getPlayerKey()) + 1;
				this.upfloatCounts.put(upfloater.getPlayerKey(), count);
			}
		}
	}

	/**
	 * compute the downfloat counts from the previous rounds
	 * 
	 * @param roundNumber
	 */
	protected void computeDlownFloatCounts(int roundNumber) {
		this.downfloatCounts = new HashMap<>();
		for (String playerKey : this.presentPlayerKeys) {
			this.downfloatCounts.put(playerKey, new Integer(0));
		}

		for (int i = 1; i < roundNumber; i++) {
			for (ChesspairingPlayer downflaoter : getRound(i).getDownfloaters()) {
				Integer count = this.downfloatCounts.get(downflaoter.getPlayerKey()) + 1;
				this.downfloatCounts.put(downflaoter.getPlayerKey(), count);
			}
		}
	}

	/**
	 * compute the partner history for the previous rounds,
	 * 
	 * @param roundNumber
	 */
	private void computePartnersHistory(int roundNumber) {
		this.partnerHistory = new HashMap<>();
		for (String playerKey : this.presentPlayerKeys) {
			List<String> newPartnerList = new ArrayList<>();
			this.partnerHistory.put(playerKey, newPartnerList);
		}

		// iterate over rounds and collect the data
		for (int i = 1; i < roundNumber; i++) {
			for (ChesspairingGame game : getRound(i).getGames()) {
				if (game.getResult() != ChesspairingResult.BYE) {
					String whiteKey = game.getWhitePlayer().getPlayerKey();
					String blackKey = game.getBlackPlayer().getPlayerKey();
					utilAddPartnerToHistory(whiteKey, blackKey);
					utilAddPartnerToHistory(blackKey, whiteKey);
				}
			}
		}
	}

	/**
	 * utility function used to populate the partners history
	 * 
	 * @param playerKey
	 * @param parnerKey
	 */
	private void utilAddPartnerToHistory(String playerKey, String parnerKey) {
		if (playerKey.equals(parnerKey)) {
			throw new IllegalStateException(playerKey + " was paired against himself! ");
		}
		List<String> partners = this.partnerHistory.get(playerKey);
		if (partners.contains(parnerKey)) {
			// for the moment just throw exception
			throw new IllegalStateException(playerKey + " was paired with " + parnerKey + " more then once");
		}
		partners.add(parnerKey);
	}

	/**
	 * compute the color history considering the previous rounds, for the moment
	 * I'm just ignoring buys
	 * 
	 * @param roundNumber
	 */
	private void computeCollorHistory(int roundNumber) {
		this.playerKeystoColorHistory = new HashMap<>();
		// create the arrays for the present players
		for (Entry<String, Double> entry : currentPoints.entrySet()) {
			String key = entry.getKey();
			List<ChesspairingColour> newList = new ArrayList<>();
			playerKeystoColorHistory.put(key, newList);
		}

		for (int i = 1; i < roundNumber; i++) {
			for (ChesspairingGame game : getRound(i).getGames()) {
				// ignoring the buy
				if (game.getResult() != ChesspairingResult.BYE) {
					String whiteKey = game.getWhitePlayer().getPlayerKey();
					if (playerKeystoColorHistory.containsKey(whiteKey)) {
						playerKeystoColorHistory.get(whiteKey).add(ChesspairingColour.WHITE);
					}
					String blackKey = game.getBlackPlayer().getPlayerKey();
					if (playerKeystoColorHistory.containsKey(blackKey)) {
						playerKeystoColorHistory.get(blackKey).add(ChesspairingColour.BLACK);
					}
				}
			}
		}
	}

	/**
	 * it collects the presents players state from the players list at the
	 * tournament level
	 * 
	 * @param roundNumber
	 */
	private void computePresentPleyers() {
		this.presentPlayerKeys = new ArrayList<>();
		List<ChesspairingPlayer> players = mTournament.getPlayers();
		for (ChesspairingPlayer player : players) {
			if (player.isPresent()) {
				this.presentPlayerKeys.add(player.getPlayerKey());
			}
		}
	}

	/**
	 * compute all players results from the previous rounds and orders the
	 * results in the descending order. 
	 * 
	 * @param roundNumber
	 */
	private void computeCurrentResults(int roundNumber) {

		// reset playersByResult;
		this.groupsByResult = new HashMap<>();
		this.orderedGroupKeys = new ArrayList<>();

		// players key
		this.currentPoints = new HashMap<>();

		for (int i = 1; i <= roundNumber; i++) {
			ChesspairingRound round = getRound(i);
			List<ChesspairingGame> games = round.getGames();
			for (ChesspairingGame game : games) {

				String whiteKey = game.getWhitePlayer().getPlayerKey();
				// if white is present
				if (this.presentPlayerKeys.contains(whiteKey)) {
					Double whitePoints = this.currentPoints.get(whiteKey);
					if (whitePoints == null) {
						whitePoints = 0.0;
						this.currentPoints.put(whiteKey, whitePoints);
					}
					if (game.getResult() == ChesspairingResult.WHITE_WINS) {
						whitePoints = whitePoints + 1;
					}
					if (game.getResult() == ChesspairingResult.BYE) {
						whitePoints = whitePoints + 0.5;
						// go to the next game
						continue;
					}
					String blackKey = game.getBlackPlayer().getPlayerKey();
					if (this.presentPlayerKeys.contains(blackKey)) {
						Double blackPoints = this.currentPoints.get(blackKey);
						if (blackPoints == null) {
							blackPoints = 0.0;
							this.currentPoints.put(blackKey, blackPoints);
						}
						if (game.getResult() == ChesspairingResult.BLACK_WINS) {
							blackPoints = blackPoints + 1;
						}
						if (game.getResult() == ChesspairingResult.DRAW_GAME) {
							whitePoints = whitePoints + 0.5;
							blackPoints = blackPoints + 0.5;
						}
						this.currentPoints.put(whiteKey, whitePoints);
						this.currentPoints.put(blackKey, blackPoints);
					}

				}
			}
			
			//iterate over present players and if no current points then add 0 points
			for (String key: presentPlayerKeys){
				if (!currentPoints.containsKey(key)){
					this.currentPoints.put(key, 0.0);
				}
			}
		}

		// simple test to validate some aspects are in order
		if (presentPlayerKeys.size() != this.currentPoints.entrySet().size()) {
			throw new IllegalStateException("Present players size not the same as currentPoints set size");
		}
		// put the results in playersByResult
		for (Entry<String, Double> entry : currentPoints.entrySet()) {
			// if playersByResult group does not exist then create it
			Double result = entry.getValue();
			if (!this.groupsByResult.containsKey(result)) {
				Map<String, ChesspairingPlayer> newGroup = new HashMap<>();
				this.groupsByResult.put(result, newGroup);
				this.orderedGroupKeys.add(result);
			}
			Map<String, ChesspairingPlayer> group = groupsByResult.get(result);
			String playerKey = entry.getKey();
			ChesspairingPlayer player = getPlayer(playerKey);
			group.put(playerKey, player);
		}
		// order the results
		Collections.reverse(this.orderedGroupKeys);
	}

	/**
	 * get a reference to the round by round number;
	 * 
	 * @param roundNumber
	 * @return
	 */
	private ChesspairingRound getRound(int roundNumber) {
		List<ChesspairingRound> rounds = mTournament.getRounds();
		for (ChesspairingRound round : rounds) {
			if (round.getRoundNumber() == roundNumber) {
				return round;
			}
		}
		throw new IllegalStateException(
				"Tournament in inconsistent state! Not able to find roundNumber = " + roundNumber);
	}

	/**
	 * it tests for the proper rounds identification (not to have 2 rounds with
	 * the same id) and if there is place to create one more round
	 * 
	 * @return true if a new round can be generated;
	 */
	private boolean canIGenerateNextRound() {
		int totalRounds = mTournament.getTotalRounds();
		int currentRouns = mTournament.getRounds().size();

		List<Integer> roundNumbers = new ArrayList<>();
		for (ChesspairingRound round : mTournament.getRounds()) {
			if (roundNumbers.contains(round.getRoundNumber())) {
				// you have 2 rounds with the same id
				return false;
			}
			roundNumbers.add(round.getRoundNumber());
		}

		if (currentRouns < totalRounds) {
			return true;
		} else {
			return false;
		}
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

        Collections.sort(mTournament.getPlayers(), new ByEloReverse());
        List<ChesspairingGame> games = new ArrayList<>();
        List<ChesspairingPlayer> players = mTournament.getPlayers();
        int count = 0;
        ChesspairingGame game = new ChesspairingGame();
        game.setTableNumber(0);
        for (ChesspairingPlayer player : players) {
            count++;
            if (count % 2 == 1) {
                int tableNumber = game.getTableNumber() + 1;
                game = new ChesspairingGame();
				game.setTableNumber(tableNumber);
				game.setWhitePlayer(player);
				if (count == players.size()) {
					game.setResult(ChesspairingResult.BYE);
					games.add(game);
				}
			} else {
				game.setBlackPlayer(player);
				game.setResult(ChesspairingResult.NOT_DECIDED);
				games.add(game);
			}
		}
		ChesspairingRound round = new ChesspairingRound();
		round.setRoundNumber(1);
		round.setGames(games);

		// is the first round so wee can create all new data
		List<ChesspairingRound> rounds = new ArrayList<>();

		// add the round
		rounds.add(round);

		// and wee set the rounds
		mTournament.setRounds(rounds);

		PairingSummary firstRoundOk = new PairingSummary();
		firstRoundOk.setShortMessage(PairingSummary.PARRING_OK);
		firstRoundOk.setLongMessage("First round was generated");
		mTournament.setParringSummary(firstRoundOk);
	}

	/**
	 * if players with no initialOrderId or the same initialOrderId then do not
	 * pare and set the paring message accordingly. No initial order is
	 * considered smaller or equal to 0
	 * 
	 * @return true if validation is OK and false otherwise
	 */
	private boolean validateOrder() {
		// players with no id
		List<String> playersNoId = new ArrayList<>();
		List<ChesspairingPlayer> players = mTournament.getPlayers();
		for (ChesspairingPlayer player : players) {
			if (player.getInitialOrderId() <= 0) {
				playersNoId.add(player.getName());
			}
		}
		if (playersNoId.size() > 0) {
			StringBuffer sb = new StringBuffer();
			sb.append("You have players with no initialOrderId: ");
			for (String name : playersNoId) {
				sb.append(name + ", ");//
			}
			int id = sb.lastIndexOf(", ");
			sb.replace(id, sb.length(), "");
			PairingSummary parringSummary = new PairingSummary();
			parringSummary.setShortMessage(PairingSummary.PARRING_NOT_OK);
			parringSummary.setLongMessage(sb.toString());
			mTournament.setParringSummary(parringSummary);
			return false;
		}

		// players with the same id
		Map<Integer, ChesspairingPlayer> map = new HashMap<>();
		for (ChesspairingPlayer player : players) {
			if (map.containsKey(player.getInitialOrderId())) {
				StringBuffer sb = new StringBuffer();
				sb.append("You have players with the same initialOrderId: ");
				sb.append(map.get(player.getInitialOrderId()).getName() + " and ");
				sb.append(player.getName());
				PairingSummary parringSummary = new PairingSummary();
				parringSummary.setShortMessage(PairingSummary.PARRING_NOT_OK);
				parringSummary.setLongMessage(sb.toString());
				mTournament.setParringSummary(parringSummary);
				return false;
			} else {
				map.put(player.getInitialOrderId(), player);
			}
		}
		return true;
	}

	private ChesspairingPlayer getPlayer(String playerKey) {
		for (ChesspairingPlayer player : mTournament.getPlayers()) {
			if (player.getPlayerKey().equals(playerKey)) {
				return player;
			}
		}
		throw new IllegalStateException("You are searching by the wrong key: " + playerKey);
	}

	private void computeNextRound(int roundNumber) {
		// remove the games from the generatedRound
		List<ChesspairingGame> games = new ArrayList<>();
		this.generatedRound = new ChesspairingRound();
		this.generatedRound.setGames(games);
		this.generatedRound.setRoundNumber(roundNumber);

		this.currentDownfloaters = new HashMap<>();

		/**
		 * start the iteration over groups in the descending order. In order to
		 * avoid thread weird behaviour because the group keys wee copy the keys
		 * before wee iterate
		 */
		List<Double> copyGroupKeys = new ArrayList<>(orderedGroupKeys);

		// while no need to downfloat then keep downfloating
		boolean someoneWasDownfloated = true;
		while (someoneWasDownfloated) {
			someoneWasDownfloated = false;
			for (Double groupKey : copyGroupKeys) {
				Map<String, ChesspairingPlayer> group = groupsByResult.get(groupKey);
				int size = group.size();
				// if modulo 2 != 0 then find a downfloater
				if ((size % 2) != 0) {
					someoneWasDownfloated = true;
					downfloatSomeoneInGroup(groupKey);
				}
			}
		}

		for (Double groupKey : copyGroupKeys) {
			/**
			 * check to make sure the group still exists
			 * this is related to bug 02
			 */
			if (this.groupsByResult.get(groupKey)== null){
				//just move on
				continue;
			}
			
			boolean paringOK = pareGroup(groupKey, roundNumber);
			if (!paringOK) {

				/*
				 * downfloat all players from group and then start again all
				 * parings. Note for the future: you should see if you have
				 * players that can be pared and then downfloat only those that
				 * can not be pared. For the moment downfloating all will do.
				 */
				// if this is the last group then join with the previous group
				if (copyGroupKeys.size() == 1) {
					throw new IllegalStateException("What should I do when I only have one group?");
				}
				Double sourceGroup = -1.0;
				Double destGroup = -1.0;
				// if this is the last index then join with the previous index
				if (copyGroupKeys.indexOf(groupKey) == copyGroupKeys.size() - 1) {
					int indexSource = copyGroupKeys.indexOf(groupKey);
					int indexDestination = indexSource - 1;
					sourceGroup = orderedGroupKeys.get(indexSource);
					destGroup = orderedGroupKeys.get(indexDestination);
				} else {
					int indexSource = copyGroupKeys.indexOf(groupKey);
					int indexDestination = indexSource + 1;
					sourceGroup = orderedGroupKeys.get(indexSource);
					destGroup = orderedGroupKeys.get(indexDestination);
				}
				joinGroups(sourceGroup, destGroup);
				// and start again
				computeNextRound(roundNumber);
			}
		}
	}

	/**
	 * It moves the players from the sourceGroup in the destGroup and then
	 * remove the sourceGroup from groupsByResut and from orderedGroupKeys
	 * 
	 * @param sourceGroup
	 * @param destGroup
	 */
	public void joinGroups(Double sourceGroup, Double destGroup) {
		Map<String, ChesspairingPlayer> sourcePlayers = groupsByResult.get(sourceGroup);
		Map<String, ChesspairingPlayer> destPlayers = groupsByResult.get(destGroup);
		// add all players to destGroup
		for (Entry<String, ChesspairingPlayer> item : sourcePlayers.entrySet()) {
			destPlayers.put(item.getKey(), item.getValue());
		}
		// remove the group
		groupsByResult.remove(sourceGroup);
		// remove from order
		orderedGroupKeys.remove(sourceGroup);
	}

	/**
	 * It tries to pare a group
	 * 
	 * @param groupKey
	 * @param roundNumber
	 * @return
	 */
	private boolean pareGroup(Double groupKey, int roundNumber) {
		Map<String, ChesspairingPlayer> group = this.groupsByResult.get(groupKey);
        //<debug>
        if (group == null) {
            System.out.println("group is null");
        }
        //</debug>
        List<ChesspairingPlayer> players = new ArrayList<>();
        for (Entry<String, ChesspairingPlayer> entry : group.entrySet()) {
            players.add(entry.getValue());
        }
        // order the group
        Collections.sort(players, new ByInitialOrderIdReverse());
        Collections.sort(players, new ByElo());
        // by points just in case it was a downfloater in the group
        Collections.sort(players, new Comparator<ChesspairingPlayer>() {
            @Override
            public int compare(ChesspairingPlayer o1, ChesspairingPlayer o2) {
                Double pointsO1 = currentPoints.get(o1.getPlayerKey());
                Double pointsO2 = currentPoints.get(o2.getPlayerKey());
                return Double.compare(pointsO1, pointsO2);
            }
		});

		if (players.size() % 2 != 0) {
			throw new IllegalStateException("You should have resolved groups count before");
		}

		/**
		 * split the list indexes and build the s1 and s2
		 */
		List<List<Integer>> split = Tools.initialSplitList(players.size());
		List<Integer> list1 = split.get(0);
		List<Integer> list2 = split.get(1);
		int size = list2.size();
		Integer[] newArray = new Integer[size];
		Integer[] s1 = new Integer[size];
		// copy the elements
		for (int i = 0; i < size; i++) {
			newArray[i] = list2.get(i);
			s1[i] = list1.get(i);
		}
		Set<Integer[]> permutations = Tools.getPermutationsV01(newArray);
		// for each permutation test if paring is valid
		Set<Integer[]> validPermutations = new HashSet<>();
		for (Integer[] s2 : permutations) {
			if (testIfPermutationIsValid(s1, s2, players)) {
				validPermutations.add(s2);
			}
		}
		if (validPermutations.size() == 0) {
			// drop the group and restart the paring. move all players down?
			// Yes!
			return false;
		}
		// for the moment just take the first permutation and pare the players
		Integer[] s2 = validPermutations.iterator().next();
		List<ChesspairingGame> games = buildGamesFromPermutation(s1, s2, players);
		this.generatedRound.getGames().addAll(games);
		return true;
	}

	private List<ChesspairingGame> buildGamesFromPermutation(Integer[] s1, Integer[] s2,
			List<ChesspairingPlayer> players) {
		List<ChesspairingGame> games = new ArrayList<>();
		for (int i = 0; i < s1.length; i++) {
			int indexA = s1[i];
			int indexB = s2[i];
			ChesspairingPlayer playerA = players.get(indexA);
			ChesspairingPlayer playerB = players.get(indexB);
			games.add(parePlayers(playerA, playerB));
		}
		return games;
	}

	/**
	 * it builds a game is something is not write will throw exception. You have
	 * to be sure that the game you will build be legal
	 * 
	 * @param playerA
	 * @param playerB
	 * @return
	 */
	private ChesspairingGame parePlayers(ChesspairingPlayer playerA, ChesspairingPlayer playerB) {
		String keyA = playerA.getPlayerKey();
		String keyB = playerB.getPlayerKey();
		if (keyA.equals(keyB)) {
			throw new IllegalStateException("You shuld never try to pare a players agains himself");
		}
		/**
		 * two players shall not meet more than once
		 */
		List<String> partnersA = partnerHistory.get(keyA);
		if (partnersA.contains(keyB)) {
			throw new IllegalStateException("two players shall not meet more than once");
		}
		List<String> partnersB = partnerHistory.get(keyB);
		if (partnersB.contains(keyA)) {
			throw new IllegalStateException("two players shall not meet more than once");
		}

		/**
		 * the color difference is the number of games played with white minus
		 * the number of games played with black
		 * 
		 * -2 < diff < 2
		 */
		int aHistoryColor = getColorDifference(keyA);
		int bHistoryColor = getColorDifference(keyB);

		// if a is white
		int aWhite = aHistoryColor + 1;
		int bBlach = bHistoryColor - 1;

		if ((-2 < aWhite) && (aWhite < 2) && (-2 < bBlach) && (bBlach < 2)) {
			// this works
			ChesspairingGame game = new ChesspairingGame();
			game.setWhitePlayer(playerA);
			game.setBlackPlayer(playerB);
			game.setTableNumber(0);
			game.setResult(ChesspairingResult.NOT_DECIDED);
			return game;
		}

		// if a is black
		int aBlack = aHistoryColor - 1;
		int bWhite = bHistoryColor + 1;
		if ((-2 < aBlack) && (aBlack < 2) && (-2 < bWhite) && (bWhite < 2)) {
			ChesspairingGame game = new ChesspairingGame();
			game.setWhitePlayer(playerB);
			game.setBlackPlayer(playerA);
			game.setTableNumber(0);
			game.setResult(ChesspairingResult.NOT_DECIDED);
			return game;
		}
		throw new IllegalStateException("You have tried to generate a game that was not legal");
	}

	/**
	 * build the games and test if the games are valid
	 * 
	 * @param s1
	 * @param s2
	 * @param players
	 */
	private boolean testIfPermutationIsValid(Integer[] s1, Integer[] s2, List<ChesspairingPlayer> players) {
		for (int i = 0; i < s1.length; i++) {
			int indexA = s1[i];
			int indexB = s2[i];
			ChesspairingGame game = new ChesspairingGame();
			game.setWhitePlayer(players.get(indexA));
			game.setBlackPlayer(players.get(indexB));
			boolean gameIsValid = testIfGameIsValid(game);
			if (!gameIsValid) {
				return false;
			}
		}
		return true;
	}

	/**
	 * it tests if the game is valid It also tires to swap players if any of the
	 * players in the initial order it plays with the same coller 3 times in a
	 * row
	 * 
	 * @param game
	 * @return
	 */
	private boolean testIfGameIsValid(ChesspairingGame game) {
		ChesspairingPlayer playerA = game.getWhitePlayer();
		String keyA = playerA.getPlayerKey();
		ChesspairingPlayer playerB = game.getBlackPlayer();
		String keyB = playerB.getPlayerKey();
		if (keyA.equals(keyB)) {
			throw new IllegalStateException("You shuld never try to pare a players agains himself");
		}
		/**
		 * two players shall not meet more than once
		 */
		List<String> partnersA = partnerHistory.get(keyA);
		if (partnersA.contains(keyB)) {
			return false;
		}
		List<String> partnersB = partnerHistory.get(keyB);
		if (partnersB.contains(keyA)) {
			return false;
		}

		/**
		 * the color difference is the number of games played with white minus
		 * the number of games played with black
		 * 
		 * -2 < diff < 2
		 */
		int aHistoryColor = getColorDifference(keyA);
		int bHistoryColor = getColorDifference(keyB);

		// if a is white
		int aWhite = aHistoryColor + 1;
		int bBlach = bHistoryColor - 1;
		boolean aPlaysWite = false;
		if ((-2 < aWhite) && (aWhite < 2) && (-2 < bBlach) && (bBlach < 2)) {
			aPlaysWite = true;
		}

		// if a is black
		boolean aPlaysBlack = false;
		if (!aPlaysWite) {
			int aBlack = aHistoryColor - 1;
			int bWhite = bHistoryColor + 1;
			if ((-2 < aBlack) && (aBlack < 2) && (-2 < bWhite) && (bWhite < 2)) {
				aPlaysBlack = true;
			}
		}

		if (!aPlaysWite && !aPlaysBlack) {
			return false;
		}

		// basic tests have passed. time to return true
		return true;
	}

	private int getColorDifference(String playerKey) {
		int diff = 0;
		List<ChesspairingColour> hisory = playerKeystoColorHistory.get(playerKey);
		for (ChesspairingColour color : hisory) {
			if (color.equals(ChesspairingColour.WHITE)) {
				diff++;
			} else {
				diff--;
			}
		}
		return diff;
	}

	/**
	 * 
	 * 
	 * @return
	 */
	private boolean downfloatSomeoneInGroup(Double groupKey) {
		int lastIndex = this.orderedGroupKeys.size() - 1;
        int thisIndex = this.orderedGroupKeys.indexOf(groupKey);
        Map<String, ChesspairingPlayer> group = groupsByResult.get(groupKey);

        // order the players
        List<ChesspairingPlayer> players = new ArrayList<>();
        for (Entry<String, ChesspairingPlayer> entry : group.entrySet()) {
            players.add(entry.getValue());
        }

        Collections.sort(players, new ByElo());
        Collections.sort(players, new ByInitialOrderIdReverse());
        Collections.sort(players, new Comparator<ChesspairingPlayer>() {
            @Override
            public int compare(ChesspairingPlayer o1, ChesspairingPlayer o2) {
                int countO1 = 0;
                if (currentDownfloaters.containsKey(o1.getPlayerKey())) {
                    countO1++;
                }
                int countO2 = 0;
                if (currentDownfloaters.containsKey(o2.getPlayerKey())) {
                    countO2++;
				}
				return Integer.compare(countO1, countO2);
			}
		});

		for (ChesspairingPlayer player : players) {
			if (!currentDownfloaters.containsKey(player.getPlayerKey())) {
				if (lastIndex == thisIndex) {
					/**
					 * this is the player that will get a buy set as buy and
					 * remove from group
					 */
					setAsBuy(player);
					group.remove(player.getPlayerKey());
					return true;
				} else {
					// increase player downfloat count
					String key = player.getPlayerKey();
					int count = downfloatCounts.get(key) + 1;
					downfloatCounts.put(key, count);
					// move player to the next group
					Double nextKey = this.orderedGroupKeys.get(thisIndex + 1);
					Map<String, ChesspairingPlayer> nextGroup = groupsByResult.get(nextKey);
					group.remove(key);
					nextGroup.put(key, player);
					return true;
				}
			}
		}

		throw new IllegalStateException("You should allways should be able to downfloat someone. Please investigate");
	}

	private void setAsBuy(ChesspairingPlayer player) {
		if (this.buyGame != null) {
			throw new IllegalStateException("You should have set a player as buy only one time!");
		}
		this.buyGame = new ChesspairingGame();
		this.buyGame.setWhitePlayer(player);
		this.buyGame.setResult(ChesspairingResult.BYE);
	}

	/**
	 * get the round number from the round object and assigns the respective to
	 * the round
	 * 
	 * @param round
	 */
	private void setRound(ChesspairingRound round) {
		int rNumber = round.getRoundNumber();
		if (rNumber < 1) {
			throw new IllegalStateException();
		}
		List<ChesspairingRound> rounds = mTournament.getRounds();
		if (rounds.size() >= rNumber) {
			rounds.remove(rNumber - 1);
		}
		rounds.add(rNumber - 1, round);
	}
}
