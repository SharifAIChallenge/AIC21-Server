package ir.sharif.aichallenge.server.logic.handlers;


import java.util.HashMap;

/**
 * Handles Logic of node owner.
 */
public class OwnerHandler {
    private HashMap<Integer, String> nodeIdToOwner;

    /**
     * Create a OwnerHandler for a game.
     */
    public OwnerHandler() {
        // TODO: initialize or change nodeIdToOwner
        this.nodeIdToOwner = new HashMap<Integer, String>();
    }

    public boolean hasOwner(int nodeId) {
        return nodeIdToOwner.containsKey(nodeId);
    }

    public String getOwnerId(int nodeId) {
        if (hasOwner(nodeId)) {
            return nodeIdToOwner.get(nodeId);
        }
        //TODO return NullOwner
        return "";
    }

    public void setOwnerId(int nodeId, String ownerId) {
        nodeIdToOwner.put(nodeId, ownerId);
    }
}
