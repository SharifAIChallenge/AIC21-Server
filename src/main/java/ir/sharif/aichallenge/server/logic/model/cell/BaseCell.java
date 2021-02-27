package ir.sharif.aichallenge.server.logic.model.cell;

import ir.sharif.aichallenge.server.logic.model.Colony.Colony;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;

import java.util.List;

public class BaseCell extends Cell {
    private Colony colony;
    public BaseCell(int xPosition, int yPosition) {
        super(xPosition, yPosition, CellType.BASE, ResourceType.NONE, -1);
    }

    public Colony getColony() {
        return colony;
    }

    public void setColony(Colony colony) {
        this.colony = colony;
    }

    @Override
    public void manageResources() {
        List<Ant> workerAnts = getWorkerAnts();
        for (Ant ant : workerAnts) {
            colony.addResource(ant.getCarryingResourceType(), ant.getCarryingResourceAmount());
            ant.setCarryingResourceType(ResourceType.NONE);
            ant.setCarryingResourceAmount(0);
        }
    }
}
