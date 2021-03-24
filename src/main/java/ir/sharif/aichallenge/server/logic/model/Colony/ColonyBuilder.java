package ir.sharif.aichallenge.server.logic.model.Colony;

import ir.sharif.aichallenge.server.logic.model.cell.BaseCell;

public class ColonyBuilder {
    private Colony colony;

    public ColonyBuilder(int id) {
        colony = new Colony(id);
    }

    public ColonyBuilder setBaseCell(BaseCell baseCell, int initialBaseHealth){
        colony.setBaseCell(baseCell, initialBaseHealth);
        return this;
    }

    public Colony getColony() {
        return colony;
    }
}