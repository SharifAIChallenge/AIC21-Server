package ir.sharif.aichallenge.server.logic.utility;

import ir.sharif.aichallenge.server.logic.model.Node;

import java.util.HashMap;

/**
 * Represents an Interface which is responsible for Creating the Map of game.
 */
public interface MapGenerator {
    HashMap<Integer, Node> generateMap(int size);
}
