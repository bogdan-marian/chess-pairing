package eu.chessdata.chesspairing.model;

import eu.chessdata.chesspairing.algoritms.comparators.ByBuchholzPointsReverse;
import eu.chessdata.chesspairing.algoritms.comparators.ByInitialOrderIdReverse;
import eu.chessdata.chesspairing.algoritms.comparators.ChainedComparator;

import java.util.*;

public class ChesspairingTournament {
    private String name;
    private String description;
    private String city;
    private String federation;
    private Date dateOfStart;
    private Date dateOfEnd;
    private String typeOfTournament;
    private String ChiefArbiter;
    private String deputyChiefArbiters;
    /**
     * this is the maximum allowed number of rounds in a tournament. If you try to
     * pare over this number some algorithms will just crash.
     */
    private int totalRounds;
    private ChesspairingByeValue chesspairingByeValue;
    private List<ChesspairingPlayer> players = new ArrayList<ChesspairingPlayer>();
    private List<ChesspairingRound> rounds = new ArrayList<ChesspairingRound>();
    private PairingSummary parringSummary;
    private List<ChesspairingStanding> chesspairingStandings = new ArrayList<>();
    private List<ChesspairingStanding.Type> tieBreaks = new ArrayList<>();

    // <getters and setters>

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

    public String getChiefArbiter() {
        return ChiefArbiter;
    }

    public void setChiefArbiter(String chiefArbiter) {
        ChiefArbiter = chiefArbiter;
    }

    public String getDeputyChiefArbiters() {
        return deputyChiefArbiters;
    }

    public void setDeputyChiefArbiters(String deputyChiefArbiters) {
        this.deputyChiefArbiters = deputyChiefArbiters;
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

    public List<ChesspairingStanding> getChesspairingStandings() {
        return chesspairingStandings;
    }

    public void setChesspairingStandings(List<ChesspairingStanding> chesspairingStandings) {
        this.chesspairingStandings = chesspairingStandings;
    }

    public List<ChesspairingStanding.Type> getTieBreaks() {
        return tieBreaks;
    }

    public void setTieBreaks(List<ChesspairingStanding.Type> tieBreaks) {
        this.tieBreaks = tieBreaks;
    }

    // </getters and setters>

    /**
     * It returns the player in {@link #players} by player key
     *
     * @param playerKey is the key of a specific player
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
     * @param indexPlayer is the index of the player
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
     * @param roundNumber is the round number for witch wee have to compute the standings
     * @return and ordered list of players. The best player is ranked number one
     */
    public List<ChesspairingPlayer> computeStandings(final int roundNumber) {
        List<ChesspairingPlayer> standings = new ArrayList<>();
        standings.addAll(this.players);


        // collect all games. for each player create a list with all the games that he
        // played
        final Map<ChesspairingPlayer, List<ChesspairingGame>> playerGames = new HashMap<>();
        // map with players this player won against
        final Map<ChesspairingPlayer, List<ChesspairingPlayer>> woneAgainst = new HashMap<>();
        for (ChesspairingPlayer player : this.players) {
            List<ChesspairingGame> games = new ArrayList<>();
            playerGames.put(player, games);
            List<ChesspairingPlayer> players = new ArrayList<>();
            woneAgainst.put(player, players);
        }
        for (int i = 1; i <= roundNumber; i++) {
            // for each
            ChesspairingRound round = getRoundByRoundNumber(i);
            for (ChesspairingPlayer player : this.getPlayers()) {
                if (!round.playerAbsent(player)) {
                    ChesspairingGame game = round.getGame(player);
                    List<ChesspairingGame> games = playerGames.get(player);
                    games.add(game);

                    if (game.playerWins(player)) {
                        ChesspairingPlayer adversery = game.getAdversary(player);
                        List<ChesspairingPlayer> trofeyList = woneAgainst.get(player);
                        trofeyList.add(adversery);
                    }
                }
            }
        }

        Comparator<ChesspairingPlayer> byPoints = new Comparator<ChesspairingPlayer>() {
            Map<ChesspairingPlayer, Float> pointsMap = computePointsUntilRound(roundNumber);

            @Override
            public int compare(ChesspairingPlayer o1, ChesspairingPlayer o2) {
                // TODO Auto-generated method stub
                Float points1 = pointsMap.get(o1);
                Float points2 = pointsMap.get(o2);
                // compare in reverce order

                return points2.compareTo(points1);
            }
        };

        Comparator<ChesspairingPlayer> byDirectMatches = new Comparator<ChesspairingPlayer>() {

            @Override
            public int compare(ChesspairingPlayer o1, ChesspairingPlayer o2) {
                List<ChesspairingPlayer> trofeyList = woneAgainst.get(o1);
                if (trofeyList.contains(o2)) {
                    return 1;
                }
                return 0;
            }
        };

        Comparator<ChesspairingPlayer> byReversedInitialOrder = new ByInitialOrderIdReverse();

        Comparator<ChesspairingPlayer> byBuchholzReverse = new ByBuchholzPointsReverse(roundNumber, this);

        ChainedComparator chainedComparator = new ChainedComparator(byPoints, byBuchholzReverse, byDirectMatches, byReversedInitialOrder);

        Collections.sort(standings, chainedComparator);

        return standings;
    }

    /**
     * @param roundNumber
     * @return
     */
    private Map<ChesspairingPlayer, Float> computePointsUntilRound(int roundNumber) {
        final Map<ChesspairingPlayer, Float> pointsMap = new HashMap<>();
        //final Map<ChesspairingPlayer, Float> pointsMap =  new HashMap<>();
        // set the points to 0
        for (ChesspairingPlayer player : this.players) {
            pointsMap.put(player, 0f);
        }

        for (int i = 1; i <= roundNumber; i++) {
            ChesspairingRound round = getRoundByRoundNumber(i);
            if (!round.allGamesHaveBeanPlayed()) {
                throw new IllegalStateException(
                        "Attempt to compute standings when there are still games with no result");
            }

            // cycle all games and collect the points
            for (ChesspairingPlayer player : this.players) {
                for (ChesspairingGame game : round.getGames()) {
                    Float initialPoints = pointsMap.get(player);
                    if (game.hasPlayer(player)) {
                        Float points = game.getPointsForPlayer(player);
                        Float result = points + initialPoints;
                        pointsMap.put(player, result);
                    }
                }
            }
        }
        return pointsMap;
    }

    /**
     * It finds the round by a specific round number. If the round requested does
     * not exist then the request it will just throw exception
     *
     * @param roundNumber of the round requested
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

    /**
     * It computes the Buchholz points won by a player ony by the game from a specific round
     *
     * @param roundNumber the round number
     * @param playerId    the player id
     * @return float value
     */
    public float getBuchholzPointsWonInRound(int roundNumber, String playerId) {
        ChesspairingRound round = this.getRoundByRoundNumber(roundNumber);
        ChesspairingPlayer player = this.getPlayer(playerId);
        if (!round.isPaired(player)) {
            return 0.0f;
        }
        ChesspairingGame game = round.getGame(player);
        return (game.getBuchholzPointsWonInGame(player, roundNumber, totalRounds));
    }

    /**
     * It computes the BuchholzPoints for a specific player in a specific round
     *
     * @param roundNumber the round number
     * @param playerId    the player id
     * @return float value
     */
    public float computeBuchholzPoints(int roundNumber, String playerId) {
        float points = 0.0f;
        for (int i = 1; i <= roundNumber; i++) {
            points += getBuchholzPointsWonInRound(i, playerId);
        }
        return points;
    }

    /**
     * It computes the points for a specific player in a specific round
     *
     * @param roundNumber
     * @param playerId
     * @return
     */
    public float computePoints(int roundNumber, String playerId) {
        float points = 0.0f;
        for (int i = 1; i <= roundNumber; i++) {
            points += getPointsWonInRound(i, playerId);
        }
        return points;
    }

    private float getPointsWonInRound(int roundNumber, String playerId) {
        ChesspairingRound round = this.getRoundByRoundNumber(roundNumber);
        ChesspairingPlayer player = this.getPlayer(playerId);
        if (!round.isPaired(player)) {
            return 0.0f;
        }
        ChesspairingGame game = round.getGame(player);
        return (game.getPointsForPlayer(player));
    }

    public ChesspairingPlayer getPlayerById(String id) {
        Optional<ChesspairingPlayer> player = getPlayers().stream()
                .filter(p -> p.getPlayerKey().equals(id)).findFirst();
        if (player.isPresent()) {
            return player.get();
        } else {
            throw new IllegalStateException("Player not found by id=" + id);
        }
    }
}
