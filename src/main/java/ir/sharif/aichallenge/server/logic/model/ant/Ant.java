package ir.sharif.aichallenge.server.logic.model.ant;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;

public class Ant {
    private int id;
    private int colonyId;
    private int health;
    private AntType antType;
    private int xPosition;
    private int yPosition;
    // just used for worker ant
    private ResourceType carryingResourceType;
    private int carryingResourceAmount;
    private int inTrap = 0;

    public Ant(int id, int colonyId, int xPosition, int yPosition, AntType antType) {
        this.id = id;
        this.colonyId = colonyId;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.antType = antType;
        setInitialHealth(antType);
        setInitialResourceSettings();
    }

    private void setInitialResourceSettings() {
        carryingResourceType = ResourceType.NONE;
        this.carryingResourceAmount = 0;
    }

    private void setInitialHealth(AntType antType) {
        health = antType == AntType.WORKER ? ConstConfigs.WORKER_ANT_INITIAL_HEALTH
                : ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH;
    }

    public int getCarryingResourceAmount() {
        return carryingResourceAmount;
    }

    public void setCarryingResourceAmount(int carryingResourceAmount) {
        this.carryingResourceAmount = carryingResourceAmount;
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

    public ResourceType getCarryingResourceType() {
        if (antType == AntType.SOLDIER)
            return ResourceType.NONE;
        return carryingResourceType;
    }

    public void setCarryingResourceType(ResourceType carryingResourceType) {
        this.carryingResourceType = carryingResourceType;
    }

    public int getInTrap() {
        return this.inTrap;
    }

    public void setIntrap(int trap) {
        this.inTrap = trap;
    }

    public void callback() {
        // handle ant callbacks each turn
    }
}
