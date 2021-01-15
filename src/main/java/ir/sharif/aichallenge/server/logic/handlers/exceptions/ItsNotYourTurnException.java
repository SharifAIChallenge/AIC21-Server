package ir.sharif.aichallenge.server.logic.handlers.exceptions;

public class ItsNotYourTurnException extends GameActionException {
    public ItsNotYourTurnException() {
        super("Action failed. It's not your turn !!!");
    }
}
