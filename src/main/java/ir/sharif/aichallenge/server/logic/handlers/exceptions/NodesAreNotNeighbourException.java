package ir.sharif.aichallenge.server.logic.handlers.exceptions;

public class NodesAreNotNeighbourException extends GameActionException {
    public NodesAreNotNeighbourException(int nodeIdToIncrease, int nodeIdToDecrease) {
        super("Invalid action. selected nodes must be neighbours for this action but nodeId=" +
                nodeIdToIncrease + " and nodeId=" + nodeIdToDecrease + " are not.");
    }
}
