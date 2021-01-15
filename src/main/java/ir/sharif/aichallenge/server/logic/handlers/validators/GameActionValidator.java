package ir.sharif.aichallenge.server.logic.handlers.validators;

import ir.sharif.aichallenge.server.logic.handlers.OwnerHandler;
import ir.sharif.aichallenge.server.logic.handlers.TurnHandler;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.*;
import ir.sharif.aichallenge.server.logic.model.Node;

import java.util.HashMap;

/**
 * Represents a class which is responsible for validating players actions.
 */
public class GameActionValidator {
    private HashMap<Integer, Node> nodes;
    private OwnerHandler ownerHandler;
    private TurnHandler turnHandler;

    /**
     * Create a GameActionValidator for a game.
     * @param nodes The map of the game.
     * @param ownerHandler Handles all stuff about owner of nodes.
     * @param turnHandler  Handles turn logic of the game.
     */
    public GameActionValidator(HashMap<Integer, Node> nodes, OwnerHandler ownerHandler, TurnHandler turnHandler) {
        this.nodes = nodes;
        this.ownerHandler = ownerHandler;
        this.turnHandler = turnHandler;
    }

    public void validateDecreaseOnePointAndGetNewNode(String playerId, int nodeIdToDecrease, int nodeIdToCatch)
            throws GameActionException {
        validateTurn(playerId);
        if (nodes.containsKey(nodeIdToDecrease))
            throw new InvalidNodeIdException(nodeIdToDecrease);
        if (nodes.containsKey(nodeIdToCatch))
            throw new InvalidNodeIdException(nodeIdToCatch);
        if (!ownerHandler.getOwnerId(nodeIdToDecrease).equals(playerId))
            throw new ItsNotYourNodeException(nodeIdToDecrease, "decrease value");
        if (ownerHandler.hasOwner(nodeIdToCatch))
            throw new NodeIsNotFreeException(nodeIdToCatch);
        if (nodes.get(nodeIdToDecrease).getValue() < 1)
            throw new NodeValueIsNotEnoughException(nodeIdToDecrease, "decrease from and catch a new node");
    }

    public void validateDecreaseOnePointAndIncreaseAnother(String playerId, int nodeIdToDecrease, int nodeIdToIncrease)
            throws GameActionException {
        validateTurn(playerId);
        if (nodes.containsKey(nodeIdToDecrease))
            throw new InvalidNodeIdException(nodeIdToDecrease);
        if (nodes.containsKey(nodeIdToIncrease))
            throw new InvalidNodeIdException(nodeIdToIncrease);
        if (!ownerHandler.getOwnerId(nodeIdToDecrease).equals(playerId))
            throw new ItsNotYourNodeException(nodeIdToDecrease, "decrease value");
        if (!ownerHandler.getOwnerId(nodeIdToIncrease).equals(playerId))
            throw new ItsNotYourNodeException(nodeIdToIncrease, "increase value");
        if (nodes.get(nodeIdToDecrease).getValue() < 1)
            throw new NodeValueIsNotEnoughException(nodeIdToDecrease, "decrease from and increase another node");
        if (!nodes.get(nodeIdToDecrease).isNeighbourWith(nodeIdToIncrease))
            throw new NodesAreNotNeighbourException(nodeIdToIncrease, nodeIdToDecrease);
    }

    public void validateTryToGetEnemyNode(String playerId, int fromNodeId, int targetNodeId)
            throws GameActionException {
        validateTurn(playerId);
        if (nodes.containsKey(fromNodeId))
            throw new InvalidNodeIdException(fromNodeId);
        if (nodes.containsKey(targetNodeId))
            throw new InvalidNodeIdException(targetNodeId);
        if (!ownerHandler.getOwnerId(fromNodeId).equals(playerId))
            throw new ItsNotYourNodeException(fromNodeId, "attack from this node");
        if (!ownerHandler.hasOwner(targetNodeId))
            throw new NodeHasNotAnOwnerException(targetNodeId);
        if (ownerHandler.getOwnerId(targetNodeId).equals(playerId))
            throw new CanNotAttackOwnNodeException(fromNodeId);
    }

    private void validateTurn(String playerId) throws ItsNotYourTurnException {
        if (!turnHandler.getCurrentPlayer().equals(playerId)) {
            throw new ItsNotYourTurnException();
        }
    }
}
