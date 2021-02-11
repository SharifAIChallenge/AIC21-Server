package ir.sharif.aichallenge.server.logic.model.cell;

public class BaseCell extends Cell {
    public BaseCell(int xPosition, int yPosition) {
        super(xPosition, yPosition, CellType.BASE, ResourceType.NONE, -1);
    }
}
