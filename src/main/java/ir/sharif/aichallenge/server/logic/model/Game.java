package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.logic.handlers.OwnerHandler;
import ir.sharif.aichallenge.server.logic.handlers.ScoreHandler;
import ir.sharif.aichallenge.server.logic.handlers.TurnHandler;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.handlers.validators.GameActionValidator;

import java.util.HashMap;

/**
 * Represents the Major class to control a game.
 */
public class Game {
    private HashMap<Integer, Node> nodes;
    private OwnerHandler ownerHandler;
    private TurnHandler turnHandler;
    private ScoreHandler scoreHandler;
    private GameActionValidator validator;
    public static final int GAME_MAXIMUM_TURN_COUNT = 50;

    /**
     * Create a Game with specific Map and Handlers.
     *
     * @param nodes        The map of the Game.
     * @param ownerHandler Handles all stuff about owner of nodes.
     * @param turnHandler  Handles turn logic of the game.
     * @param scoreHandler The scoring system.
     * @param validator    The validator for players actions.
     */
    public Game(HashMap<Integer, Node> nodes, OwnerHandler ownerHandler, TurnHandler turnHandler
            , ScoreHandler scoreHandler, GameActionValidator validator) {
        this.nodes = nodes;
        this.ownerHandler = ownerHandler;
        this.turnHandler = turnHandler;
        this.scoreHandler = scoreHandler;
        this.validator = validator;
    }

    public void decreaseOnePointAndGetNewNode(String playerId, int nodeIdToDecrease, int nodeIdToCatch)
            throws GameActionException {
        validator.validateDecreaseOnePointAndGetNewNode(playerId, nodeIdToDecrease, nodeIdToCatch);

        nodes.get(nodeIdToDecrease).decreaseOnePoint();

        nodes.get(nodeIdToCatch).setValue(1);
        ownerHandler.setOwnerId(nodeIdToCatch, playerId);
        turnHandler.increaseTurn();
        scoreHandler.increaseOnePoint(playerId);
    }

    public void decreaseOnePointAndIncreaseAnother(String playerId, int nodeIdToDecrease, int nodeIdToIncrease)
            throws GameActionException {
        validator.validateDecreaseOnePointAndIncreaseAnother(playerId, nodeIdToDecrease, nodeIdToIncrease);

        nodes.get(nodeIdToDecrease).decreaseOnePoint();
        nodes.get(nodeIdToIncrease).increaseOnePoint();
        turnHandler.increaseTurn();
    }

    public void tryToGetEnemyNode(String playerId, int fromNodeId, int targetNodeId) throws GameActionException {
        validator.validateTryToGetEnemyNode(playerId, fromNodeId, targetNodeId);

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
