package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.common.network.data.ClientMessageInfo;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.logic.handlers.OwnerHandler;
import ir.sharif.aichallenge.server.logic.handlers.ScoreHandler;
import ir.sharif.aichallenge.server.logic.handlers.TurnHandler;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.handlers.validators.GameActionValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Major class to control a game.
 */
public class Game {
    private HashMap<Integer, Node> nodes;
    private OwnerHandler ownerHandler;
    private TurnHandler turnHandler;
    private ScoreHandler scoreHandler;
    private GameActionValidator validator;
    // messages to be sent to clients in this turn
    private Message[] clientTurnMessages;

    public static final int GAME_MAXIMUM_TURN_COUNT = 50;


    /**
     * Create a Game with specific Map and Handlers.
     *
     * @param nodes The map of the Game.
     *              //     * @param ownerHandler Handles all stuff about owner of nodes.
     *              //     * @param turnHandler  Handles turn logic of the game.
     *              //     * @param scoreHandler The scoring system.
     *              //     * @param validator    The validator for players actions.
     */
    public Game(HashMap<Integer, Node> nodes, List<String> players) {
        this.nodes = nodes;
        this.ownerHandler = new OwnerHandler();
        this.turnHandler = new TurnHandler(players);
        this.scoreHandler = new ScoreHandler(players);
        this.validator = new GameActionValidator(nodes, ownerHandler, turnHandler);
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

    public int getTurn() {
        return turnHandler.getCurrentTurn();
    }

    public Message[] getClientTurnMessages() {
        return clientTurnMessages;
    }

    public Node getNodeByID(int id) {
        // TODO: get a node by id
        return nodes.get(id);
    }

    public void passTurn(Map<String, List<ClientMessageInfo>> messages) {
        // TODO: based on the messages in MessageTypes, do changes in game
        // for example:
        //      increment turn
        //      generate messages for clients and add them to [clientTurnMessages]
        //      set [isGameFinished] when necessary
        //      and many many other things :)

    }
}
