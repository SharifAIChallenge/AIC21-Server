package ir.sharif.aichallenge.server.logic.model.cell;

import java.util.ArrayList;
import java.util.List;

import ir.sharif.aichallenge.server.logic.model.ant.Ant;

public class Cell {
    protected int xPosition;
    protected int yPosition;
    public CellType cellType;
    protected ResourceType resourceType;
    protected int resourceAmount;
    protected List<Ant> ants;

    public Cell(int xPosition, int yPosition, CellType cellType, ResourceType resourceType, int resourceAmount) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.cellType = cellType;
        this.resourceType = resourceType;
        this.resourceAmount = resourceAmount;
        ants = new ArrayList<>();
    }

    public void AddAnt(Ant ant) {
        ants.add(ant);
    }

    public void RemoveAnt(Ant ant) {
        ants.remove(ant);
    }

    public int getX() {
        return xPosition;
    }

    public int getY() {
        return yPosition;
    }

    public CellType getCellType() {
        return cellType;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public int getResourceAmount() {
        if (resourceType == ResourceType.NONE)
            return -1;
        return resourceAmount;
    }

    public List<Ant> getAnts() {
        return ants;
    }

    public void increaseResource(int amount) {
        resourceAmount += amount;
    }

    public void decreaseResource(int amount) {
        resourceAmount -= amount;
    }

    public boolean isBase(){
        return cellType == CellType.BASE;
    }

}
