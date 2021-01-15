package ir.sharif.aichallenge.server.logic.handlers.exceptions;

public class CanNotAttackOwnNodeException extends GameActionException {
    public CanNotAttackOwnNodeException(int fromNodeId) {
        super("Invalid Action. Can not Attack yourself node with nodeId=" + fromNodeId);
    }
}
