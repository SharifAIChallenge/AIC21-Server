package ir.sharif.aichallenge.server.logic.model.map;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.MoveType;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.cell.CellType;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;

import java.util.*;

public class GameMap {
    private Cell[][] cells;
    // y axis
    private int width;
    // x axis
    private int height;

    public GameMap(Cell[][] cells, int width, int height) {
        this.cells = cells;
        this.width = width;
        this.height = height;
    }

    public Cell getCell(int xPosition, int yPosition) {
        return cells[((yPosition % width) + width) % width][((xPosition % height) + height) % height];
    }

    public Cell[] getViewableCells(int xPosition, int yPosition) {
        return getAroundCells(xPosition, yPosition, ConstConfigs.MAX_VIEW_DISTANCE);
    }

    public Cell[] getAttackableCells(int xPosition, int yPosition) {
        return getAroundCells(xPosition, yPosition, ConstConfigs.MAX_ATTACK_DISTANCE);
    }

    private Cell[] getAroundCells(int xPosition, int yPosition, int maxDistance) {
        List<Cell> aroundCells = new ArrayList<>();
        for (int i = xPosition - maxDistance; i <= xPosition + maxDistance; i++) {
            for (int j = yPosition - maxDistance; j <= yPosition + maxDistance; j++) {
                if (getManhattanDistance(i, j, xPosition, yPosition) > maxDistance)
                    continue;
                aroundCells.add(getCell(i, j));
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
                        continue;
                    Cell cell = getCell(j, k);

                    if(cell.getResourceType() == ResourceType.NONE){
                        cell.setResourceType(resourceType);
                        cell.setResourceAmount(0);
                    }

                    if (resourceType == cell.getResourceType()) {
                        cells[k][j].increaseResource(resourceAmount);
                        return;
                    }
                }
            }
        }
    }

    private int getManhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    public List<Cell> getAllCells() {
        List<Cell> allCells = new ArrayList<>();
        for (Cell[] rowCells : cells) {
            allCells.addAll(Arrays.asList(rowCells));
        }
        return allCells;
    }

    public void changeAntCurrentCell(Ant ant, int moveType) {
        int newX = ant.getXPosition();
        int newY = ant.getYPosition();
        switch (moveType) {
            case MoveType.UP:
                newY -= 1;
                break;
            case MoveType.DOWN:
                newY += 1;
                break;
            case MoveType.LEFT:
                newX -= 1;
                break;
            case MoveType.RIGHT:
                newX += 1;
                break;
            default:
                return;
        }
        newX = newX % getHeight();
        newY = newY % getWidth();
        Cell targetCell = getCell(newX, newY);
        if (targetCell.cellType == CellType.WALL)
            return;

        changeAntCurrentCell(ant, targetCell);
    }

    private void changeAntCurrentCell(Ant ant, Cell targetCell) {
        getCell(ant.getXPosition(), ant.getYPosition()).removeAnt(ant);
        ant.moveTo(targetCell.getX(), targetCell.getY());
        targetCell.addAnt(ant);
    }
}
