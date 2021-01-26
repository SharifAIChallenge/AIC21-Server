package ir.sharif.aichallenge.server.logic.dto.payloads;

public class Actions {
    int actionType;
    String firstNodeId;
    String secondNodeId;

    public Actions(int actionType, String firstNodeId, String secondNodeId) {
        this.actionType = actionType;
        this.firstNodeId = firstNodeId;
        this.secondNodeId = secondNodeId;
    }
}
