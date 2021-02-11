package ir.sharif.aichallenge.server.logic.model.map;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;

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

    public Cell[] getViewableCells(int xPosition, int yPosition) {
        return getAroundCells(xPosition, yPosition, ConstConfigs.MAX_VIEW_DISTANCE);

    }

    public Cell[] getAttackableCells(int xPosition, int yPosition) {
        return getAroundCells(xPosition, yPosition, ConstConfigs.MAX_ATTACK_DISTANCE);
    }

    private Cell[] getAroundCells(int xPosition, int yPosition, int maxDistance) {
        ArrayList<Cell> aroundCells = new ArrayList<>();
        for (int i = xPosition - maxDistance; i <= xPosition + maxDistance; i++) {
            for (int j = yPosition - maxDistance; j <= yPosition + maxDistance; j++) {
                if (Math.abs(i - xPosition) + Math.abs(j - yPosition) > maxDistance)
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
