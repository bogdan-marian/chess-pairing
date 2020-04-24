package eu.chessdata.chesspairing.model;

import java.nio.channels.IllegalSelectorException;

public class ChesspairingGame {

    /**
     * it allays starts from 1
     */
    private int tableNumber;

    private ChesspairingPlayer whitePlayer;
    private ChesspairingPlayer blackPlayer;

    /**
     * 0 still not decided, 1 white player wins, 2 black player wins, 3 draw game, 4 buy
     */
    private ChesspairingResult result = ChesspairingResult.NOT_DECIDED;

    public ChesspairingGame(int tableNumber, ChesspairingPlayer whitePlayer, ChesspairingPlayer blackPlayer) {
        this.tableNumber = tableNumber;
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.result = ChesspairingResult.NOT_DECIDED;
    }

    public ChesspairingGame() {
    }

    @Override
    public String toString() {
        String whitePlayerKey = whitePlayer.getPlayerKey();
        String blackPlayerKey = "buy";
        if (this.result != ChesspairingResult.BYE) {
            blackPlayerKey = blackPlayer.getPlayerKey();
        }
        return (String.valueOf(tableNumber) + "(" + whitePlayerKey + "-" + blackPlayerKey + ")");
    }

    // getters and setters
    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public ChesspairingPlayer getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(ChesspairingPlayer whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public ChesspairingPlayer getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(ChesspairingPlayer blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

    public ChesspairingResult getResult() {
        return result;
    }

    public void setResult(ChesspairingResult result) {
        this.result = result;
    }

    /**
     * Static method used to build a buy game
     *
     * @param tableNumber
     * @param whitePlayer
     * @return
     */
    public static ChesspairingGame buildBuyGame(int tableNumber, ChesspairingPlayer whitePlayer) {
        ChesspairingGame buyGame = new ChesspairingGame();
        buyGame.tableNumber = tableNumber;
        buyGame.whitePlayer = whitePlayer;
        buyGame.result = ChesspairingResult.BYE;
        return buyGame;
    }

    /**
     * Returns true if the player wins. It throws error if player not in the game. A
     * player is not consider as wining if the game is a buy
     *
     * @param player that wee are interested if it won or not
     * @return true of false
     */
    public boolean playerWins(ChesspairingPlayer player) {
        ChesspairingColour collor = ChesspairingColour.BUY;
        if (player.equals(getWhitePlayer())) {
            collor = ChesspairingColour.WHITE;
        }
        if (this.getBlackPlayer() != null && player.equals(getBlackPlayer())) {
            collor = ChesspairingColour.BLACK;
        }
        switch (this.result) {
            case NOT_DECIDED:
                throw new IllegalStateException("Game not decided. You should not compute points on not finished rounds");
            case BYE:
                return false;
            case WHITE_WINS:
                if (collor.equals(ChesspairingColour.WHITE)) {
                    return true;
                }
                break;
            case WHITE_WINS_BY_FORFEIT:
                if (collor.equals(ChesspairingColour.WHITE)) {
                    return true;
                }
                break;
            case BLACK_WINS:
                if (collor.equals(ChesspairingColour.BLACK)) {
                    return true;
                }
                break;
            case BLACK_WINS_BY_FORFEIT:
                if (collor.equals(ChesspairingColour.BLACK)) {
                    return true;
                }
                break;

            default:
                break;
        }
        return false;
    }

    /**
     * It returns the adversary of this player. It throws error if there is this is
     * a buy game
     *
     * @param player that wee are searching for his adversary
     * @return the adversary of the interested player
     */
    public ChesspairingPlayer getAdversary(ChesspairingPlayer player) {
        if (this.result == ChesspairingResult.BYE) {
            throw new IllegalStateException("This is a buy game");
        }
        if (player.equals(getWhitePlayer())) {
            return getBlackPlayer();
        }
        if (player.equals(getBlackPlayer())) {
            return getWhitePlayer();
        }
        // wee should never reach this point
        throw new IllegalSelectorException();
    }

    public boolean hasPlayer(ChesspairingPlayer player) {
        if (this.whitePlayer.equals(player)) {
            return true;
        }
        if (this.result != ChesspairingResult.BYE) {
            if (this.blackPlayer.equals(player)) {
                return true;
            }
        }
        return false;
    }

    public float getPointsForPlayer(ChesspairingPlayer player) {
        if (this.whitePlayer.equals(player)) {
            if (result == ChesspairingResult.WHITE_WINS ||
                    result == ChesspairingResult.WHITE_WINS_BY_FORFEIT ||
                    result == ChesspairingResult.BYE) {
                return 1.0f;
            } else if (result == ChesspairingResult.DRAW_GAME) {
                return 0.5f;
            } else {
                return 0.0f;
            }
        }
        if (this.result != ChesspairingResult.BYE) {
            if (this.blackPlayer.equals(player)) {
                if (result == ChesspairingResult.BLACK_WINS ||
                        result == ChesspairingResult.BLACK_WINS_BY_FORFEIT) {
                    return 1.0f;
                } else if (result == ChesspairingResult.DRAW_GAME) {
                    return 0.5f;
                } else {
                    return 0.0f;
                }
            }
        }
        throw new IllegalStateException("Player not in game. Player name = " + player.getName());
    }
}
