package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;

import java.util.ArrayList;

public class GameMap {
    private Cell[][] cells;
    private int width;
    private int height;

    public GameMap(Cell[][] cells, int width, int height) {
        this.cells = cells;
        this.width = width;
        this.height = height;
    }

    public Cell getCell(int xPosition, int yPosition) {
        return cells[yPosition][xPosition];
    }

    public Cell[] getAroundCells(int xPosition, int yPosition) {
        ArrayList<Cell> aroundCells = new ArrayList<>();
        for (int i = xPosition - ConstConfigs.MAX_VIEW_DISTANCE; i <= xPosition + ConstConfigs.MAX_VIEW_DISTANCE; i++) {
            for (int j = yPosition - ConstConfigs.MAX_VIEW_DISTANCE; j <= yPosition + ConstConfigs.MAX_VIEW_DISTANCE; j++) {
                if (Math.abs(i - xPosition) + Math.abs(j - yPosition) > ConstConfigs.MAX_VIEW_DISTANCE)
                    continue;
                aroundCells.add(cells[j][i]);
            }
        }
        return aroundCells.toArray(new Cell[0]);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
