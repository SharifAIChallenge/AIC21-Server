package ir.sharif.aichallenge.server.logic.utility;

import ir.sharif.aichallenge.server.logic.model.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

/**
 * Represents a MapGenerator which generates a random Map with specific number of nodes.
 */
public class RandomMapGenerator implements MapGenerator {
    private Random rand = new Random();

    public HashMap<Integer, Node> generateMap(int size) {
        HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> neighbours = getNeighbours(nodes.values());
            Node newNode = new Node(i, neighbours, 0);
            nodes.put(i, newNode);
        }
        return nodes;
    }

    private ArrayList<Integer> getNeighbours(Collection<Node> nodes) {
        ArrayList<Integer> neighbours = new ArrayList<Integer>();
        for (Node node : nodes) {
            if (rand.nextBoolean()) {
                neighbours.add(node.getId());
            }
        }
        return neighbours;
    }
}
