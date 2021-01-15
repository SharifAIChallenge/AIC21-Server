package ir.sharif.aichallenge.server.logic.handlers.exceptions;


public class InvalidNodeIdException extends GameActionException {
    public InvalidNodeIdException(int nodeId) {
        super("Invalid node id. Can not found a node with id=" + nodeId);
    }
}
