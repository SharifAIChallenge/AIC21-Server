package ir.sharif.aichallenge.server.logic.model.cell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;

public class Cell {
    private int xPosition;
    private int yPosition;
    public CellType cellType;
    private ResourceType resourceType;
    private int resourceAmount;
    private List<Ant> ants;
    private Random random;

    public Cell(int xPosition, int yPosition, CellType cellType, ResourceType resourceType, int resourceAmount) {
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.cellType = cellType;
        this.resourceType = resourceType;
        this.resourceAmount = resourceAmount;
        ants = new ArrayList<>();
        random = new Random();
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
            return -1;
        return resourceAmount;
    }

    public void increaseResource(int amount) {
        resourceAmount += amount;
    }

    public void decreaseResource(int amount) {
        resourceAmount -= amount;
    }

    public boolean isBase() {
        return cellType == CellType.BASE;
    }

    public List<Ant> getWorkerAnts() {
        return ants.stream().filter(x -> x.getAntType() == AntType.WORKER).collect(Collectors.toList());
    }

    public void manageResources() {
        if (cellType == CellType.WALL)
            return;
        List<Ant> freeWorkerAnts = getWorkerAnts().stream()
                .filter(x -> x.getCarryingResourceType() == ResourceType.NONE)
                .collect(Collectors.toList());
        if (freeWorkerAnts.size() <= getResourceAmount()) {
            decreaseResource(freeWorkerAnts.size());
            for (Ant ant : freeWorkerAnts) {
                ant.setCarryingResourceAmount(1);
                ant.setCarryingResourceType(getResourceType());
            }
        } else {
            for (int i = 0; i < getResourceAmount(); i++) {
                int randomIndex = random.nextInt(freeWorkerAnts.size());
                Ant ant = freeWorkerAnts.get(randomIndex);
                freeWorkerAnts.remove(randomIndex);
                ant.setCarryingResourceAmount(1);
                ant.setCarryingResourceType(getResourceType());
            }
        }
    }
}
