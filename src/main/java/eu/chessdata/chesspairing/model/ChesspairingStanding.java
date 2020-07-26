package eu.chessdata.chesspairing.model;

public class ChesspairingStanding {
    private int rank;
    private ChesspairingPlayer player;
    private float bucholtz;
    private float gamesWon;
    private float progressive;
    private float sunnebornBerger;

    public ChesspairingStanding() {
        // default constructor
    }

    // <getters and setters>

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public ChesspairingPlayer getPlayer() {
        return player;
    }

    public void setPlayer(ChesspairingPlayer player) {
        this.player = player;
    }

    public float getBucholtz() {
        return bucholtz;
    }

    public void setBucholtz(float bucholtz) {
        this.bucholtz = bucholtz;
    }

    public float getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(float gamesWon) {
        this.gamesWon = gamesWon;
    }

    public float getProgressive() {
        return progressive;
    }

    public void setProgressive(float progressive) {
        this.progressive = progressive;
    }

    public float getSunnebornBerger() {
        return sunnebornBerger;
    }

    public void setSunnebornBerger(float sunnebornBerger) {
        this.sunnebornBerger = sunnebornBerger;
    }


    // <getters and setters>
}
