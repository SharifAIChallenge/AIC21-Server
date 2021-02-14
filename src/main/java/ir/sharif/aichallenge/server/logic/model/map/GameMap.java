package ir.sharif.aichallenge.server.logic.model.map;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;

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
                if (getManhattanDistance(i, j, xPosition, yPosition) > maxDistance)
                    continue;
                aroundCells.add(cells[j % width][i % height]);
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

    public void addResource(ResourceType resourceType, int resourceAmount, int xPos, int yPos) {
        for (int i = 0; i <= Math.min(width, height) / 2; i++) {
            for (int j = xPos - i; j <= xPos + i; j++) {
                for (int k = yPos - i; k <= yPos + i; k++) {
                    if (getManhattanDistance(j, k, xPos, yPos) > i)
                        return;
                    if (resourceType == cells[k % width% height][j].getResourceType()) {
                        cells[k][j].increaseResource(resourceAmount);
                        return;
                    }
                }
            }
        }
    }

    public int getManhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }
}
