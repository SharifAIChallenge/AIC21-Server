package ir.sharif.aichallenge.server.logic.handlers.exceptions;

public class NodeIsNotFreeException extends GameActionException {
    public NodeIsNotFreeException(int nodeIdToCatch) {
        super("Selected node with id=" + nodeIdToCatch + " is not free and has an owner.");
    }
}
