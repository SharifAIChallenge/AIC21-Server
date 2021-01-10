package ir.sharif.aichallenge.server.logic.entities;

import ir.sharif.aichallenge.server.logic.handlers.OwnerHandler;
import ir.sharif.aichallenge.server.logic.handlers.ScoreHandler;
import ir.sharif.aichallenge.server.logic.handlers.TurnHandler;

import java.util.HashMap;

public class Game {
    private HashMap<Integer, Node> nodes;
    private OwnerHandler ownerHandler;
    private TurnHandler turnHandler;
    private ScoreHandler scoreHandler;
    public static final int GAME_MAXIMUM_TURN_COUNT = 50;

    public Game(HashMap<Integer, Node> nodes, OwnerHandler ownerHandler, TurnHandler turnHandler, ScoreHandler scoreHandler) {
        this.nodes = nodes;
        this.ownerHandler = ownerHandler;
        this.turnHandler = turnHandler;
        this.scoreHandler = scoreHandler;
    }

    public void decreaseOnePointAndGetNewNode(String playerId, int nodeIdToDecrease, int nodeIdToCatch) {
        //TODO add validator
        nodes.get(nodeIdToDecrease).decreaseOnePoint();

        nodes.get(nodeIdToCatch).setValue(1);
        ownerHandler.setOwnerId(nodeIdToCatch, playerId);
        turnHandler.increaseTurn();
        scoreHandler.increaseOnePoint(playerId);
    }

    public void decreaseOnePointAndIncreaseAnother(String playerId, int nodeIdToDecrease, int nodeIdToIncrease) {
        //TODO add validator
        nodes.get(nodeIdToDecrease).decreaseOnePoint();
        nodes.get(nodeIdToIncrease).increaseOnePoint();
        turnHandler.increaseTurn();
    }

    public void tryToGetEnemyNode(String playerId, int fromNodeId, int targetNodeId) {
        //TODO add validator
        Node fromNode = nodes.get(fromNodeId);
        Node targetNode = nodes.get(targetNodeId);
        if (fromNode.getValue() > targetNode.getValue()) {
            scoreHandler.decreaseOnePoint(ownerHandler.getOwnerId(targetNodeId));
            scoreHandler.increaseOnePoint(playerId);

            targetNode.setValue(fromNode.getValue());
            ownerHandler.setOwnerId(targetNodeId, playerId);
        }

        turnHandler.increaseTurn();
    }

    public boolean isCurrentPlayer(String playerId) {
        return playerId.equals(turnHandler.getCurrentPlayer());
    }

    public void playExpiredTurn() {
        turnHandler.increaseTurn();
    }

    public boolean isFinished() {
        return turnHandler.getCurrentTurn() == GAME_MAXIMUM_TURN_COUNT;
    }
}

//class ExpireTask extends TimerTask {
//    private Game callbackClass;
//
//    ExpireTask(Game callbackClass) {
//        this.callbackClass = callbackClass;
//    }
//
//    public void run() {
//        callbackClass.turnTimeExpired();
//    }
//}
