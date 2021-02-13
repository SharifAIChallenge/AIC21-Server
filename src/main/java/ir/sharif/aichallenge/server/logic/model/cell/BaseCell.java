package ir.sharif.aichallenge.server.logic.model.cell;

import ir.sharif.aichallenge.server.logic.model.Colony;

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
}
