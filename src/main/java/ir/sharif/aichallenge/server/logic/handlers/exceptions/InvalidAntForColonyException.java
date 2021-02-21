package ir.sharif.aichallenge.server.logic.handlers.exceptions;

public class InvalidAntForColonyException extends GameActionException {

    public InvalidAntForColonyException(String message) {
        super("Ant colony id does not match with the colony id.");
    }

}