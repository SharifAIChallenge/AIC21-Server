package ir.sharif.aichallenge.server.logic.model.map;

import java.util.HashMap;

import ir.sharif.aichallenge.server.logic.model.Colony;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.cell.CellType;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;

public class MapGenerator {
    public static MapGeneratorResult generateRandomMap(int width, int height) {
        Cell[][] cells = new Cell[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                // TODO: add resourceTypes and cellTypes
                cells[i][j] = new Cell(j, i, CellType.EMPTY, ResourceType.NONE, 0);
            }
        }
        GameMap map = new GameMap(cells, width, height);
        cells[0][0].cellType = CellType.BASE;
        cells[5][5].cellType = CellType.BASE;
        Colony firstColony = new Colony(0, cells[0][0], 100);
        Colony secondColony = new Colony(1, cells[5][5], 100);
        HashMap<Integer, Colony> colonies = new HashMap<>();
        colonies.put(0, firstColony);
        colonies.put(1, secondColony);
        return new MapGeneratorResult(map, colonies);
    }

    public static GameMap generateFromFile(String fileName) {
        // TODO
        return null;
    }

    public static class MapGeneratorResult {
        public GameMap map;
        public HashMap<Integer, Colony> colonies;

        MapGeneratorResult(GameMap map, HashMap<Integer, Colony> colonies) {
            this.map = map;
            this.colonies = colonies;
        }
    }
}
