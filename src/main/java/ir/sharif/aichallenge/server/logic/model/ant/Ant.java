package ir.sharif.aichallenge.server.logic.model.ant;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;

public class Ant {
    private int id;
    private int colonyId;
    private int health;
    private AntType antType;
    // TODO: use Cell instead of x, y
    private int xPosition;
    private int yPosition;
    private ResourceType resourceType;
    private int resourceAmount;

    public Ant(int id, int colonyId, int xPosition, int yPosition, AntType antType) {
        this.id = id;
        this.colonyId = colonyId;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.antType = antType;
        health = antType == AntType.WORKER ?
                ConstConfigs.WORKER_ANT_INITIAL_HEALTH :
                ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH;
        resourceType = ResourceType.NONE;
        this.resourceAmount = 0;
    }

    public int getResourceAmount() {
        return resourceAmount;
    }

    public void setResourceAmount(int resourceAmount) {
        this.resourceAmount = resourceAmount;
    }

    public void moveTo(int newX, int newY) {
        xPosition = newX;
        yPosition = newY;
    }

    public int getId() {
        return id;
    }

    public int getColonyId() {
        return colonyId;
    }

    public int getHealth() {
        return health;
    }

    public void decreaseHealth(int amount) {
        health -= amount;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public int getXPosition() {
        return xPosition;
    }

    public int getYPosition() {
        return yPosition;
    }

    public AntType getAntType() {
        return antType;
    }

    public ResourceType getResourceType() {
        if (antType == AntType.SOLDIER)
            return ResourceType.NONE;
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }
}



