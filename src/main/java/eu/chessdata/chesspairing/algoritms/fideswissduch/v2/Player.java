package eu.chessdata.chesspairing.algoritms.fideswissduch.v2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import eu.chessdata.chesspairing.model.ChesspairingPlayer;

public class Player {
	protected static final Comparator<Player> byPoints = new Comparator<Player>() {
		@Override
		public int compare(Player o1, Player o2) {
			Double po1 = o1.getPairingPoints();
			Double po2 = o2.getPairingPoints();
			return -1 * po1.compareTo(po2);
		}
	};

	protected static final Comparator<Player> byElo = new Comparator<Player>() {
		@Override
		public int compare(Player o1, Player o2) {
			Integer e1 = o1.getElo();
			Integer e2 = o2.getElo();
			return -1 * e1.compareTo(e2);
		}
	};

	protected static final Comparator<Player> byInitialRanking = new Comparator<Player>() {
		@Override
		public int compare(Player o1, Player o2) {
			Integer r1 = o1.getInitialRanking();
			Integer r2 = o2.getInitialRanking();
			return r1.compareTo(r2);
		}
	};

	protected String playerKey;
	protected String name;
	protected Integer initialRanking;
	protected Integer elo;
	protected List<Integer> colourHistory;
	protected List<String> playersHistory;
	protected Double pairingPoints;
	protected FloatingState floatingState;

	public Player(ChesspairingPlayer player, int initialRanking) {
		this.playerKey = player.getPlayerKey();
		this.name = player.getName();
		this.initialRanking = initialRanking;
		this.elo = player.getElo();
		this.playersHistory = new ArrayList<>();
		this.colourHistory = new ArrayList<>();
		this.pairingPoints = 0.0;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Player: " + this.playerKey + " " + this.name + "\n\t initialRanking:\t" + initialRanking
				+ "\n\t elo:\t" + elo + "\n\t points:\t" + pairingPoints + "\n\t colourHistory:\t" + colourHistory
				+ "\n\t playersHistory:\t");
		for (String key : playersHistory) {
			sb.append(key + ", ");
		}
		return sb.toString();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPlayerKey() {
		return playerKey;
	}

	public void setPlayerKey(String playerKey) {
		this.playerKey = playerKey;
	}

	public List<Integer> getColourHistory() {
		return colourHistory;
	}

	public void setColourHistory(List<Integer> colourHistory) {
		this.colourHistory = colourHistory;
	}

	public Integer getInitialRanking() {
		return initialRanking;
	}

	public void setInitialRanking(Integer initialRanking) {
		this.initialRanking = initialRanking;
	}

	public Integer getElo() {
		return elo;
	}

	public void setElo(Integer elo) {
		this.elo = elo;
	}

	public List<String> getPlayersHistory() {
		return playersHistory;
	}

	public void setPlayersHistory(List<String> playersHistory) {
		this.playersHistory = playersHistory;
	}

	public Double getPairingPoints() {
		return pairingPoints;
	}

	public void setPairingPoints(Double pairingPoints) {
		this.pairingPoints = pairingPoints;
	}

	public FloatingState getFloatingState() {
		return floatingState;
	}

	public void setFloatingState(FloatingState floatingState) {
		this.floatingState = floatingState;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((playerKey == null) ? 0 : playerKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (playerKey == null) {
			if (other.playerKey != null)
				return false;
		} else if (!playerKey.equals(other.playerKey))
			return false;
		return true;
	}
}
