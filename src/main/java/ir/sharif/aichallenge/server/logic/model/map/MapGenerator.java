package ir.sharif.aichallenge.server.logic.model.map;

import java.util.HashMap;
import java.util.Random;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.Colony.Colony;
import ir.sharif.aichallenge.server.logic.model.cell.BaseCell;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.cell.CellType;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;

public class MapGenerator {
    private static final double breadCellProb = 0.2;
    private static final double grassCellProb = 0.2;
    private static final double wallCellProb = 0.2;
    private static Random random = new Random();
    private static int playersCount;

    //cells[width][height]
    public static MapGeneratorResult generateRandomMap(int width, int height) {
        Cell[][] cells = new Cell[width][height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int cellTypeSelector = random.nextInt();
                Cell newCell;

                if (cellTypeSelector < breadCellProb)
                    newCell = new Cell(i, j, CellType.EMPTY, ResourceType.BREAD, 1);
                else if (cellTypeSelector < breadCellProb + grassCellProb)
                    newCell = new Cell(i, j, CellType.EMPTY, ResourceType.GRASS, 1);
                else if (cellTypeSelector < breadCellProb + grassCellProb + wallCellProb)
                    newCell = new Cell(i, j, CellType.WALL, ResourceType.NONE, 0);
                else
                    newCell = new Cell(i, j, CellType.EMPTY, ResourceType.NONE, 0);

                cells[j][i] = newCell;
            }
        }
        // add resources here (Type, Value)
        cells[1][0].setResourceType(ResourceType.BREAD);
        cells[1][0].setResourceAmount(2);

        GameMap map = new GameMap(cells, width, height);
        cells[0][0] = new BaseCell(0, 0);
        cells[5][5] = new BaseCell(5, 5);
        Colony firstColony = new Colony(0, (BaseCell) cells[0][0], 100);
        Colony secondColony = new Colony(1, (BaseCell) cells[5][5], 100);
        ((BaseCell) (cells[0][0])).setColony(firstColony);
        ((BaseCell) (cells[5][5])).setColony(secondColony);
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
