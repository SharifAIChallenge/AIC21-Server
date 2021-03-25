package ir.sharif.aichallenge.server.logic.model.map;

import ir.sharif.aichallenge.server.logic.model.cell.BaseCell;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExternalMap {
    private Cell[][] cells;
    private List<BaseCell> baseCells;

    public ExternalMap(Cell[][] cells) {
        this.cells = cells;
        this.baseCells = new ArrayList<>();
    }

    public void addBaseCell(BaseCell baseCell){
        baseCells.add(baseCell);
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public List<BaseCell> getBaseCells() {
        return baseCells;
    }

    public List<BaseCell> getUnAllocatedBaseCells(){
        return baseCells.stream().filter(x->x.getColony() == null).collect(Collectors.toList());
    }
}
