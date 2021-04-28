package ir.sharif.aichallenge.server.logic.model.cell;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Cell {
    private int xPosition;
    private int yPosition;
    public CellType cellType;
    private ResourceType resourceType;
    private int resourceAmount;
    private List<Ant> ants;
    private int toBeAddedTurn = -1;
    private ResourceType toBeAddedResourceType;
    private int toBeAddedResourceAmount;

    public Cell(int xPosition, int yPosition, CellType cellType, ResourceType resourceType, int resourceAmount) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.cellType = cellType;
        this.resourceType = resourceType;
        this.resourceAmount = resourceAmount;
        ants = new ArrayList<>();
    }

    // for future resource
    public Cell(int xPosition, int yPosition, ResourceType resourceType, int resourceAmount, int toBeAddedTurn) {
        this(xPosition, yPosition, CellType.EMPTY, ResourceType.NONE, 0);
        this.toBeAddedResourceType = resourceType;
        this.resourceType = ResourceType.NONE;
        this.resourceAmount = 0;
        this.toBeAddedResourceAmount = resourceAmount;
        this.toBeAddedTurn = toBeAddedTurn;
    }

    public void renew(GameMap map, int currentTurn) {
        if (currentTurn != this.toBeAddedTurn)
            return;
        map.addResource(toBeAddedResourceType, toBeAddedResourceAmount, xPosition, yPosition);
        this.toBeAddedTurn = -1;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public void setResourceAmount(int resourceAmount) {
        this.resourceAmount = resourceAmount;
    }

    public List<Ant> getAnts() {
        return ants;
    }

    public void addAnt(Ant ant) {
        ants.add(ant);
    }

    public void removeAnt(Ant ant) {
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
            return 0;
        return resourceAmount;
    }

    public void increaseResource(int amount) {
        resourceAmount += amount;
    }

    private void decreaseResource(int amount) {
        resourceAmount -= amount;
        if (resourceAmount == 0)
            resourceType = ResourceType.NONE;
        if (resourceAmount < 0)
            throw new RuntimeException("negative resource type !!!");
    }

    public boolean isBase() {
        return cellType == CellType.BASE;
    }

    public List<Ant> getWorkerAnts() {
        return ants.stream().filter(x -> x.getAntType() == AntType.WORKER).collect(Collectors.toList());
    }

    public void manageResources() {
        if (cellType == CellType.WALL || resourceType == ResourceType.NONE)
            return;
        List<Ant> freeWorkerAnts = getWorkerAnts().stream()
                .filter(x -> x.getCarryingResourceType() == ResourceType.NONE
                        || (x.getCarryingResourceType() == this.getResourceType()
                                && x.getCarryingResourceAmount() < ConstConfigs.WORKER_MAX_CARRYING_RESOURCE_AMOUNT))
                .collect(Collectors.toList());

        Collections.shuffle(freeWorkerAnts);
        for (Ant ant : freeWorkerAnts) {
            if (this.getResourceAmount() <= 0) {
                break;
            }

            int wantedAmount = ConstConfigs.WORKER_MAX_CARRYING_RESOURCE_AMOUNT - ant.getCarryingResourceAmount();
            if (wantedAmount > this.getResourceAmount()) {
                ant.setCarryingResourceAmount(ant.getCarryingResourceAmount() + this.getResourceAmount());
                ant.setCarryingResourceType(this.getResourceType());
                this.decreaseResource(this.getResourceAmount());
            } else {
                ant.setCarryingResourceAmount(ant.getCarryingResourceAmount() + wantedAmount);
                ant.setCarryingResourceType(this.getResourceType());
                this.decreaseResource(wantedAmount);
            }
        }
    }
}
