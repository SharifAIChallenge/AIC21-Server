package ir.sharif.aichallenge.server.logic.handlers.exceptions;

public class ItsNotYourNodeException extends GameActionException {
    public ItsNotYourNodeException(int nodeId, String action) {
        super("You aren't the owner of node with id=" + nodeId + " And can not " + action);
    }
}
