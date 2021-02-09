package ir.sharif.aichallenge.server.logic.utility;

import java.util.*;

/**
 * Represents a MapGenerator which generates a random GameMap with specific number of nodes.
 */
public class RandomMapGenerator implements MapGenerator {
    private Random rand = new Random();

    public HashMap<Integer, Node> generateMap(int size) {
        HashMap<Integer, Node> nodes = new HashMap<Integer, Node>();
        for (int i = 0; i < size; i++) {
            ArrayList<String> neighbours = getNeighbours(nodes.values());
            Node newNode = new Node(UUID.randomUUID().toString(), neighbours, 0);
            nodes.put(i, newNode);
        }
        return nodes;
    }

    private ArrayList<String> getNeighbours(Collection<Node> nodes) {
        ArrayList<String> neighbours = new ArrayList<String>();
        for (Node node : nodes) {
            if (rand.nextBoolean()) {
                neighbours.add(node.getId());
            }
        }
        return neighbours;
    }
}
