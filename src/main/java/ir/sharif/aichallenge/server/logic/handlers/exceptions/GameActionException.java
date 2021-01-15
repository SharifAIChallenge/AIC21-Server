package ir.sharif.aichallenge.server.logic.handlers.exceptions;

/**
 * Represents custom exception for handling players actions.
 */
public abstract class GameActionException extends Exception {
    /**
     * Creates a new Subclass of GameActionException.
     * @param message Exception message.
     */
    public GameActionException(String message) {
        super(message);
    }
}

