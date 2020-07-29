package eu.chessdata.chesspairing.model;

public class ChesspairingStanding {
    enum Type {
        bucholtz, bucholtzCut1, gamesWon, progressive, sunnebornBerger,
        directEncounter, gamesPlayedWithBlack, averageRatingOpp
    }

    private int rank;
    private ChesspairingPlayer player;
    private float points;
    private float bucholtz;
    private float bucholtzCut1;
    private int gamesWon;
    private float progressive;
    private float sunnebornBerger;
    private float directEncounter;
    private int gamesPlayedWithBlack;
    private float averageRatingOpp;


    public ChesspairingStanding() {
        // default constructor
    }

    // <getters and setters>


    public float getPoints() {
        return points;
    }

    public void setPoints(float points) {
        this.points = points;
    }

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

    public float getBucholtzCut1() {
        return bucholtzCut1;
    }

    public void setBucholtzCut1(float bucholtzCut1) {
        this.bucholtzCut1 = bucholtzCut1;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(int gamesWon) {
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

    public float getDirectEncounter() {
        return directEncounter;
    }

    public void setDirectEncounter(float directEncounter) {
        this.directEncounter = directEncounter;
    }

    public int getGamesPlayedWithBlack() {
        return gamesPlayedWithBlack;
    }

    public void setGamesPlayedWithBlack(int gamesPlayedWithBlack) {
        this.gamesPlayedWithBlack = gamesPlayedWithBlack;
    }

    public float getAverageRatingOpp() {
        return averageRatingOpp;
    }

    public void setAverageRatingOpp(float averageRatingOpp) {
        this.averageRatingOpp = averageRatingOpp;
    }


    // <getters and setters>
}
