package ir.sharif.aichallenge.server.logic.handlers;


import java.util.HashMap;

public class OwnerHandler {
    private HashMap<Integer, String> nodeIdToOwner;

    public OwnerHandler() {
        this.nodeIdToOwner = new HashMap<>();
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
