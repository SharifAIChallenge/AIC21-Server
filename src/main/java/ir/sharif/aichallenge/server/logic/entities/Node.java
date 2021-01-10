package ir.sharif.aichallenge.server.logic.entities;


import java.util.ArrayList;

public class Node {
    private int id;
    private ArrayList<Integer> neighbourIds;
    private int value;

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
