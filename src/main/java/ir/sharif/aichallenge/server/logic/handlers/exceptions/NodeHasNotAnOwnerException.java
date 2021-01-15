package ir.sharif.aichallenge.server.logic.handlers.exceptions;

public class NodeHasNotAnOwnerException extends GameActionException {
    public NodeHasNotAnOwnerException(int targetNodeId) {
        super("Invalid action. Selected node with nodeId=" + targetNodeId + "doesn't have an owner.");
    }
}
