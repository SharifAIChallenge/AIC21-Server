package ir.sharif.aichallenge.server.logic.handlers;


import java.util.ArrayList;
import java.util.HashMap;

public class ScoreHandler {
    private HashMap<String, Integer> playersScore;

    public ScoreHandler(ArrayList<String> playersIds) {
        playersScore = new HashMap<>();

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
        return new HashMap<>(playersScore);
    }
}
