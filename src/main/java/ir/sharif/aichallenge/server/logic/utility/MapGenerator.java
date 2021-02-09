package ir.sharif.aichallenge.server.logic.utility;

import java.util.HashMap;

/**
 * Represents an Interface which is responsible for Creating the GameMap of game.
 */
public interface MapGenerator {
    HashMap<Integer, Node> generateMap(int size);
}
