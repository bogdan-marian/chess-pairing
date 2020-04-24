package eu.chessdata.chesspairing.model;

import eu.chessdata.chesspairing.algoritms.fideswissduch.FideSwissDutchAlgorithmV1;

import java.util.Date;

public class ChesspairingPlayer {
    private String name;
    private int elo;
    /**
     * this is the the natural order that you wish to be considered for a
     * tournament. {@link FideSwissDutchAlgorithmV1} also uses this number in
     * making paring decisions.
     */
    private int initialOrderId;

	private String playerKey;
	private boolean isPresent;
	private double acceleratedPoints;
	private double tournamentPoints;
	private ChesspairingSex sex;
	private Date birthDate;
	private int rank;
	private ChesspairingTitle title;
	private String federation;
	private String fideNumber;

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.playerKey;
	}

	// getters and setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFederation() {
		return federation;
	}

	public void setFederation(String federation) {
		this.federation = federation;
	}

	public int getElo() {
		return elo;
	}

	public ChesspairingTitle getTitle() {
		return title;
	}

	public void setTitle(ChesspairingTitle title) {
		this.title = title;
	}

	public void setElo(int elo) {
		this.elo = elo;
	}

	public String getPlayerKey() {
		return playerKey;
	}

	public String getFideNumber() {
		return fideNumber;
	}

	public void setFideNumber(String fideNumber) {
		this.fideNumber = fideNumber;
	}

	public void setPlayerKey(String playerKey) {
		this.playerKey = playerKey;
	}

	public int getInitialOrderId() {
		return initialOrderId;
	}

	public void setInitialOrderId(int initialOrderId) {
		this.initialOrderId = initialOrderId;
	}

	public void setAcceleratedPoints(int acceleratedPoints) {
		this.acceleratedPoints = acceleratedPoints;
	}

	public boolean isPresent() {
		return isPresent;
	}

	public void setPresent(boolean isPresent) {
		this.isPresent = isPresent;
	}

	public void setTournamentPoints(int tournamentPoints) {
		this.tournamentPoints = tournamentPoints;
	}

	public ChesspairingSex getSex() {
		return sex;
	}

	public void setSex(ChesspairingSex sex) {
		this.sex = sex;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public double getAcceleratedPoints() {
		return acceleratedPoints;
	}

	public void setAcceleratedPoints(double acceleratedPoints) {
		this.acceleratedPoints = acceleratedPoints;
	}

	public double getTournamentPoints() {
		return tournamentPoints;
	}

	public void setTournamentPoints(double tournamentPoints) {
		this.tournamentPoints = tournamentPoints;
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
		ChesspairingPlayer other = (ChesspairingPlayer) obj;
		if (playerKey == null) {
			if (other.playerKey != null)
				return false;
		} else if (!playerKey.equals(other.playerKey))
			return false;
		return true;
	}
}
