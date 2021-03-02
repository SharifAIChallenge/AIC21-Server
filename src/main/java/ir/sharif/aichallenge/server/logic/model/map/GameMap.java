package ir.sharif.aichallenge.server.logic.model.map;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.MoveType;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.cell.CellType;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;

import java.util.*;

public class GameMap {
    //cells[yAxisLength][xAxisLength]
    private Cell[][] cells;
    private int yAxisLength;
    private int xAxisLength;

    public GameMap(Cell[][] cells, int yAxisLength, int xAxisLength) {
        this.cells = cells;
        this.yAxisLength = yAxisLength;
        this.xAxisLength = xAxisLength;
    }

    public Cell getCell(int xPosition, int yPosition) {
        return cells[((yPosition % yAxisLength) + yAxisLength) % yAxisLength][((xPosition % xAxisLength) + xAxisLength) % xAxisLength];
    }

    public Cell[] getAntViewableCells(int xPosition, int yPosition) {
        return getAroundCells(xPosition, yPosition, ConstConfigs.ANT_MAX_VIEW_DISTANCE);
    }

    public Cell[] getAttackableCells(int xPosition, int yPosition, int maxDistance) {
        return getAroundCells(xPosition, yPosition, maxDistance);
    }

    private Cell[] getAroundCells(int xPosition, int yPosition, int maxDistance) {
        List<Cell> aroundCells = new ArrayList<>();
        for (int x = xPosition - maxDistance; x <= xPosition + maxDistance; x++) {
            for (int y = yPosition - maxDistance; y <= yPosition + maxDistance; y++) {
                if (getManhattanDistance(x, y, xPosition, yPosition) > maxDistance)
                    continue;
                aroundCells.add(getCell(x, y));
            }
        }
        return aroundCells.toArray(new Cell[0]);
    }

    public int getYAxisLength() {
        return yAxisLength;
    }

    public int getXAxisLength() {
        return xAxisLength;
    }

    public void addResource(ResourceType resourceType, int resourceAmount, int xPos, int yPos) {
        for (int i = 0; i <= Math.min(yAxisLength, xAxisLength) / 2; i++) {
            for (int x = xPos - i; x <= xPos + i; x++) {
                for (int y = yPos - i; y <= yPos + i; y++) {
                    if (getManhattanDistance(x, y, xPos, yPos) > i)
                        continue;
                    Cell cell = getCell(x, y);

                    if (cell.getResourceType() == ResourceType.NONE) {
                        cell.setResourceType(resourceType);
                        cell.setResourceAmount(0);
                    }

                    if (resourceType == cell.getResourceType()) {
                        cell.increaseResource(resourceAmount);
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
        newX = newX % getXAxisLength();
        newY = newY % getYAxisLength();
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
