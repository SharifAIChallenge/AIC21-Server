package ir.sharif.aichallenge.server.logic.model;


import java.util.ArrayList;

/** Represents a node.
 */
public class Node {
    private int id;
    private ArrayList<Integer> neighbourIds;
    private int value;

    /**
     * Create a node with initial value and fixed id
     * @param id The node's id.
     * @param neighbourIds id of nodes which have a link to/from this node.
     * @param value The node's value.
     */
    public Node(int id, ArrayList<Integer> neighbourIds, int value) {
        this.id = id;
        this.neighbourIds = neighbourIds;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Integer> getNeighbourIds() {
        return neighbourIds;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void decreaseOnePoint() {
        value -= 1;
    }

    public void increaseOnePoint() {
        value += 1;
    }
}
