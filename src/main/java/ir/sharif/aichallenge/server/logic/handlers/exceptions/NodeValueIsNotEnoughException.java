package ir.sharif.aichallenge.server.logic.handlers.exceptions;

public class NodeValueIsNotEnoughException extends GameActionException {
    public NodeValueIsNotEnoughException(int nodeIdToDecrease, String action) {
        super("Selected node with id=" + nodeIdToDecrease + " doesn't have enough value to " + action + ".");
    }
}
