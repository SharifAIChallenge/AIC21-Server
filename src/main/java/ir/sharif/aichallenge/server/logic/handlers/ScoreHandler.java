package ir.sharif.aichallenge.server.logic.handlers;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Handles scoring of a game.
 */
public class ScoreHandler {
    private HashMap<String, Integer> playersScore;

    /**
     * Create a ScoreHandler for a game with specific users.
     *
     * @param playersIds Id of players who are attending in the Game.
     */
    public ScoreHandler(ArrayList<String> playersIds) {
        playersScore = new HashMap<String, Integer>();

        for (String playerId : playersIds) {
            playersScore.put(playerId, 0);
        }
    }

    public void increaseOnePoint(String playerId) {
        int oldPoint = playersScore.get(playerId);
        playersScore.put(playerId, oldPoint + 1);
    }

    public void decreaseOnePoint(String playerId) {
        int oldPoint = playersScore.get(playerId);
        playersScore.put(playerId, oldPoint - 1);
    }

    public HashMap<String, Integer> getScores() {
        return new HashMap<String, Integer>(playersScore);
    }
}
