package eu.chessdata.chesspairing.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ChesspairingRound {
    private int roundNumber;
    private List<ChesspairingPlayer> presentPlayers = new ArrayList<>();
    private List<ChesspairingPlayer> absentPlayers = new ArrayList<>();
    private List<ChesspairingPlayer> upfloaters = new ArrayList<>();
    private List<ChesspairingPlayer> downfloaters = new ArrayList<>();
    private List<ChesspairingGame> games = new ArrayList<>();

    /**
     * A round has games if the games list is not null or is size is grater then
     * zero
     *
     * @return boolean value
     */
    public boolean hasGames() {
        if (this.games == null) {
            return false;
        }
        if (this.games.size() == 0) {
            return false;
        }
        return true;
    }

    /**
     * It checks if the player is part of the absentPlayers list If everything fails
     * then it check if there is any game that this player is part of. If no game
     * found then the player is absent
     *
     * @return true when the player is part of the list and false when the player is
     * not part of the absent list
     */
    public boolean playerAbsent(ChesspairingPlayer player) {
        if (absentPlayers == null) {
            throw new IllegalStateException("Absent plaeyrs ware not initialized");
        }
        if (absentPlayers.contains(player)) {
            return true;
        }
        if (this.hasGames()) {
            for (ChesspairingGame game : this.games) {
                if (game.getWhitePlayer().equals(player)) {
                    return false;
                }
                if (game.getBlackPlayer() != null && game.getBlackPlayer().equals(player)) {
                    return false;
                }
            }
            // no game found player is absent
            return true;
        } else {
            // wee consider as present just in case it was added later in the tournament
            return false;
        }
    }


    /**
     * It ads a player to the list of absent players. If the player is also in the
     * list with present players It will remove it from there.
     *
     * @param player
     */
    public void addAbsentPlayer(ChesspairingPlayer player) {

        if (presentPlayers == null) {
            presentPlayers = new ArrayList<>();
        }
        if (absentPlayers == null) {
            absentPlayers = new ArrayList<>();
        }

        if (this.presentPlayers.contains(player)) {
            int index = this.presentPlayers.indexOf(player);
            this.presentPlayers.remove(index);
        }

        if (this.absentPlayers.contains(player)) {
            return;
        } else {
            this.absentPlayers.add(player);
        }
    }

    /**
     * It adds a player to the list of present players. If the player is also in the
     * list with absent players It will remove it from there
     *
     * @param player
     */
    public void addPresentPlayer(ChesspairingPlayer player) {

        if (presentPlayers == null) {
            presentPlayers = new ArrayList<>();
        }
        if (absentPlayers == null) {
            absentPlayers = new ArrayList<>();
        }

        if (this.absentPlayers.contains(player)) {
            int index = this.absentPlayers.indexOf(player);
            this.absentPlayers.remove(index);
        }

        if (this.presentPlayers.contains(player)) {
            return;
        } else {
            this.absentPlayers.add(player);
        }
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public List<ChesspairingPlayer> getAbsentPlayers() {
        return absentPlayers;
    }

    public void setAbsentPlayers(List<ChesspairingPlayer> absentPlayers) {
        this.absentPlayers = absentPlayers;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public List<ChesspairingGame> getGames() {
        return games;
    }

    public void setGames(List<ChesspairingGame> games) {
        this.games = games;
    }

    public List<ChesspairingPlayer> getUpfloaters() {
        return upfloaters;
    }

    public void setUpfloaters(List<ChesspairingPlayer> upfloaters) {
        this.upfloaters = upfloaters;
    }

    public List<ChesspairingPlayer> getPresentPlayers() {
        return presentPlayers;
    }

    public void setPresentPlayers(List<ChesspairingPlayer> presentPlayers) {
        this.presentPlayers = presentPlayers;
    }

    public List<ChesspairingPlayer> getDownfloaters() {
        return downfloaters;
    }

    public void setDownfloaters(List<ChesspairingPlayer> downfloaters) {
        this.downfloaters = downfloaters;
    }

    /**
     * It finds the game for a specific player and returns the game
     *
     * @param player that will be located
     * @return a game
     * @throws IllegalStateException if the player was not paired
     */
    public ChesspairingGame getGame(ChesspairingPlayer player) {
        for (ChesspairingGame game : games) {
            if (game.getWhitePlayer().equals(player)) {
                return game;
            }
            if (game.getResult() != ChesspairingResult.BYE) {
                if (game.getBlackPlayer().equals(player)) {
                    return game;
                }
            }
        }
        throw new IllegalStateException("No game found");
    }

    /**
     * It builds a round
     *
     * @param nextRoundNumber is the next round number
     * @param games           is the list with the games
     * @return a round
     */
    public static ChesspairingRound buildRound(int nextRoundNumber, List<ChesspairingGame> games) {
        ChesspairingRound round = new ChesspairingRound();
        round.roundNumber = nextRoundNumber;

        // compute present players
        round.presentPlayers = new ArrayList<>();
        for (ChesspairingGame game : games) {
            round.presentPlayers.add(game.getWhitePlayer());
            if (game.getResult() != ChesspairingResult.BYE) {
                round.presentPlayers.add(game.getBlackPlayer());
            }
        }

        round.games = games;

        // just set empty downfloaters and upfloaters
        round.downfloaters = new ArrayList<>();
        round.upfloaters = new ArrayList<>();

        return round;
    }

    /**
     * A round is considered that it has absent players only if the list with absent
     * players is not null. In case the tournament is peared by considering the
     * present players you should make sure in advance that you populate the absent
     * list
     *
     * @return true if the list of absent players contains at least 1 player
     */
    public boolean hasAbsentPlayers() {
        if (this.absentPlayers == null) {
            return false;
        }
        if (this.absentPlayers.size() <= 0) {
            return false;
        }
        // the round has some absent players
        return true;
    }

    /**
     * It returns true if the round contains the mains and all the
     * results are different then {@link ChesspairingResult#NOT_DECIDED}
     *
     * @return
     */
    public boolean allGamesHaveBeanPlayed() {
        if (this.games.size() == 0) {
            return false;
        }
        for (ChesspairingGame game : this.games) {
            if (game.getResult() == ChesspairingResult.NOT_DECIDED) {
                return false;
            }
        }
        return true;
    }

    /**
     * It will compute the number of points collected by a player for this round. It will
     * throw exceition if the round contains games with result "not decided"
     *
     * @param player
     * @param byeValue
     * @return
     */
    @Deprecated
    public Float getPointsFor(ChesspairingPlayer player, ChesspairingByeValue byeValue) {
        for (ChesspairingGame game : games) {
            if (player.equals(game.getWhitePlayer())) {
                switch (game.getResult()) {
                    case BYE:
                        return byeValue.getValue();
                    case WHITE_WINS:
                        return 1f;
                    case WHITE_WINS_BY_FORFEIT:
                        return 1f;
                    case NOT_DECIDED:
                        throw new IllegalStateException("Game not finished");
                    case DRAW_GAME:
                        return 0.5f;
                    case BLACK_WINS:
                        return 0f;
                    case BLACK_WINS_BY_FORFEIT:
                        return 0f;

                    default:
                        throw new IllegalStateException("Game result not treated");
                }
            }

            if (game.getBlackPlayer() != null) {
                if (player.equals(game.getBlackPlayer())) {
                    switch (game.getResult()) {
                        case BLACK_WINS:
                            return 1f;
                        case BLACK_WINS_BY_FORFEIT:
                            return 1f;
                        case WHITE_WINS:
                            return 1f;
                        case WHITE_WINS_BY_FORFEIT:
                            return 1f;
                        case DRAW_GAME:
                            return 0.5f;
                        case BYE:
                            return byeValue.getValue();
                        case NOT_DECIDED:
                            throw new IllegalStateException("Game not finished");
                        default:
                            break;
                    }
                }
            }


        }

        //no games found just return 0
        return 0f;
    }

    /**
     * Returns the opponent of a specific player
     *
     * @param playerId the id of the player
     * @return the opponent as an option in case the player has an opponent
     */
    public Optional<ChesspairingPlayer> getOpponent(String playerId) {
        for (ChesspairingGame game : this.games) {
            if (game.getWhitePlayer().getPlayerKey().equals(playerId)) {
                if (game.getResult() == ChesspairingResult.BYE) {
                    return Optional.empty();
                }
                return Optional.of(game.getBlackPlayer());
            }
            if (game.getResult() != ChesspairingResult.BYE) {
                if (game.getBlackPlayer().getPlayerKey().equals(playerId)) {
                    return Optional.of(game.getWhitePlayer());
                }
            }
        }
        return Optional.empty();
    }

    /**
     * It decides if a specific player is bye in this round
     *
     * @param playerId
     * @return
     */
    public boolean playerIsBye(String playerId) {
        for (ChesspairingGame game : this.games) {
            if (game.getResult() == ChesspairingResult.BYE) {
                ChesspairingPlayer whitePlayer = game.getWhitePlayer();
                if (whitePlayer.getPlayerKey().equals(playerId)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * If checks if a player if paired in this round
     *
     * @param player the player
     * @return true if the player if paired and false otherwise
     */
    public boolean isPaired(ChesspairingPlayer player) {
        return this.presentPlayers.contains(player);
    }

    /**
     * It checks if this round has game for a specific table number
     *
     * @param tableNumber
     * @return
     */
    public boolean hasGameForTable(Integer tableNumber) {
        Optional<ChesspairingGame> optionalGame = this.games.stream()
                .filter(game -> game.getTableNumber() == tableNumber)
                .findFirst();
        return optionalGame.isPresent();
    }
}
