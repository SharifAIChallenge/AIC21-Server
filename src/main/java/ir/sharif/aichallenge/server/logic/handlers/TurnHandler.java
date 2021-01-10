package ir.sharif.aichallenge.server.logic.handlers;

public class TurnHandler {
    private int currentTurn;
    private String[] playerIds;

    public TurnHandler(String[] playerIds) {
        currentTurn = 1;
        this.playerIds = playerIds;
    }

    public void increaseTurn() {
        currentTurn += 1;
    }

    public String getCurrentPlayer() {
        int currentPlayerIndicator = (currentTurn - 1) % playerIds.length;
        return playerIds[currentPlayerIndicator];
    }

    public int getCurrentTurn() {
        return currentTurn;
    }
}
