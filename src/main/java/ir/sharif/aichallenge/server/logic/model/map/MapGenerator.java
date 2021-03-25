package ir.sharif.aichallenge.server.logic.model.map;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.Colony.Colony;
import ir.sharif.aichallenge.server.logic.model.cell.BaseCell;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.cell.CellType;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;
import ir.sharif.aichallenge.server.logic.utility.JsonUtility;
import org.json.simple.parser.ParseException;

public class MapGenerator {
    private static final double breadCellProb = 2;
    private static final double grassCellProb = 2;
    private static final double wallCellProb = 1;
    private static Random random = new Random();
    private static int playersCount;

    public static MapGeneratorResult generateRandomMap() {
        int height = ConstConfigs.MAP_HEIGHT;
        int width = ConstConfigs.MAP_WIDTH;
        Cell[][] cells = new Cell[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int cellTypeSelector = random.nextInt(10);
                Cell newCell;

                if (cellTypeSelector < breadCellProb)
                    newCell = new Cell(j, i, CellType.EMPTY, ResourceType.BREAD, 1);
                else if (cellTypeSelector < breadCellProb + grassCellProb)
                    newCell = new Cell(j, i, CellType.EMPTY, ResourceType.GRASS, 1);
                else if (cellTypeSelector < breadCellProb + grassCellProb + wallCellProb)
                    newCell = new Cell(j, i, CellType.WALL, ResourceType.NONE, 0);
                else
                    newCell = new Cell(j, i, CellType.EMPTY, ResourceType.NONE, 0);

                cells[i][j] = newCell;
            }
        }

        GameMap map = new GameMap(cells, height, width);
        int base1x = 0;
        int base1y = 0;
        int base2x = (width - 1) / 2;
        int base2y = (height - 1) / 2;
        cells[base1y][base1x] = new BaseCell(base1x, base1y);
        cells[base2y][base2x] = new BaseCell(base2x, base2y);
        Colony firstColony = new Colony(0, (BaseCell) cells[base1y][base1x], ConstConfigs.BASE_INIT_HEALTH);
        Colony secondColony = new Colony(1, (BaseCell) cells[base2y][base2x], ConstConfigs.BASE_INIT_HEALTH);
        ((BaseCell) (cells[base1y][base1x])).setColony(firstColony);
        ((BaseCell) (cells[base2y][base2x])).setColony(secondColony);
        HashMap<Integer, Colony> colonies = new HashMap<>();
        colonies.put(0, firstColony);
        colonies.put(1, secondColony);
        return new MapGeneratorResult(map, colonies);
    }

    public static MapGeneratorResult generateFromFile(String fileName) {
        int height = ConstConfigs.MAP_HEIGHT;
        int width = ConstConfigs.MAP_WIDTH;
        try {
            ExternalMap externalMap = JsonUtility.readMapFromFile(fileName, height, width);
            GameMap gameMap = new GameMap(externalMap.getCells(), height, width);
            List<BaseCell> baseCells = externalMap.getUnAllocatedBaseCells();
            if (baseCells.size() < 2) {
                Log.e("MapGenerator", "There should be two base cells in map.json!");
                System.exit(-1);
            }
            Colony firstColony = new Colony(0, baseCells.get(0), ConstConfigs.BASE_INIT_HEALTH);
            Colony secondColony = new Colony(1, baseCells.get(1), ConstConfigs.BASE_INIT_HEALTH);
            baseCells.get(0).setColony(firstColony);
            baseCells.get(1).setColony(secondColony);

            HashMap<Integer, Colony> colonies = new HashMap<>();
            colonies.put(0, firstColony);
            colonies.put(1, secondColony);
            return new MapGeneratorResult(gameMap, colonies);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
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
