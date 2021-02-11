package ir.sharif.aichallenge.server.logic.handlers.exceptions;

public class ColonyNotExistsException extends GameActionException {

    public ColonyNotExistsException(String message, int id) {
        super("Colony with id: " + id + " does not exists in game.");
    }

}
