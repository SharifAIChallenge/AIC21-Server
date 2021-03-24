package ir.sharif.aichallenge.server.logic.model.map;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.Colony.ColonyBuilder;
import ir.sharif.aichallenge.server.logic.model.cell.BaseCell;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.cell.CellType;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapBuilder {
    private GameMap gameMap;
    private static Random random = new Random();
    private static final double breadCellProb = 2;
    private static final double grassCellProb = 2;
    private static final double wallCellProb = 1;
    private ArrayList<Cell> unAllocatedBases;

    public MapBuilder() {
        this.gameMap = new GameMap();
        unAllocatedBases = new ArrayList<>();
    }

    public MapBuilder(Cell[][] cells, int yAxisLength, int xAxisLength) {
        gameMap = new GameMap(cells, yAxisLength, xAxisLength);
        unAllocatedBases = new ArrayList<>();
    }

    public MapBuilder setCells(Cell[][] cells, ArrayList<Cell> unAllocatedBases, int yAxisLength, int xAxisLength) {
        gameMap.setCells(cells, yAxisLength, xAxisLength);
        this.unAllocatedBases = unAllocatedBases;
        return this;
    }

    public MapBuilder generateRandomMap() {
        return this.generateRandomMap(ConstConfigs.MAP_HEIGHT, ConstConfigs.MAP_WIDTH);
    }

    public MapBuilder generateRandomMap(int yAxisLength, int xAxisLength) {
        Cell[][] cells = new Cell[yAxisLength][xAxisLength];
        for (int i = 0; i < yAxisLength; i++) {
            for (int j = 0; j < xAxisLength; j++) {
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

        unAllocatedBases.add(cells[0][0]);
        unAllocatedBases.add(cells[(yAxisLength - 1) / 2][(xAxisLength - 1) / 2]);

        gameMap.setCells(cells, yAxisLength, xAxisLength);
        return this;
    }

    public MapBuilder addColony(ColonyBuilder colonyBuilder, int colonyInitialBaseHealth) {
        if (unAllocatedBases.isEmpty()) {
            throw new IllegalStateException("error in GameMapBuilder. can not add colony due to lack of unAllocatedBases");
        }
        Cell unAllocatedBaseCell = unAllocatedBases.get(0);
        unAllocatedBases.remove(0);

        int x = unAllocatedBaseCell.getX();
        int y = unAllocatedBaseCell.getY();

        addColony(colonyBuilder, colonyInitialBaseHealth, x, y);
        return this;
    }

    public MapBuilder addColony(ColonyBuilder colonyBuilder, int colonyInitialBaseHealth, int x, int y) {
        if (gameMap.getCell(x, y) instanceof BaseCell) {
            throw new IllegalStateException("error in GameMapBuilder. selected cell is base of another colony");
        }
        unAllocatedBases.removeIf(cell -> cell.getX() == x && cell.getY() == y);

        BaseCell baseCell = new BaseCell(x, y);
        gameMap.setCell(x, y, baseCell);
        colonyBuilder.setBaseCell(baseCell, colonyInitialBaseHealth);
        baseCell.setColony(colonyBuilder.getColony());
        return this;
    }

    public GameMap build() {
        return gameMap;
    }
}
