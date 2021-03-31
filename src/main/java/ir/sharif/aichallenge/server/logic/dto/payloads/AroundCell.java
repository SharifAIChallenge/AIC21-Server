package ir.sharif.aichallenge.server.logic.dto.payloads;

import java.util.List;

import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;

public class AroundCell {
    int cell_x;
    int cell_y;
    int cell_type;
    int resource_value;
    int resource_type;
    AntDTO[] ants;

    public AroundCell(Cell cell, Ant currentAnt) {
        this.cell_x = cell.getX() - currentAnt.getXPosition();
        this.cell_y = cell.getY() - currentAnt.getYPosition();
        this.cell_type = cell.getCellType().getValue();
        this.resource_value = cell.getResourceAmount();
        this.resource_type = cell.getResourceType().getValue();
        List<Ant> cellAnts = cell.getAnts();
        AntDTO[] ants = new AntDTO[cellAnts.size()];
        for (int i = 0; i < cellAnts.size(); i++) {
            ants[i] = new AntDTO(cellAnts.get(i), currentAnt);
        }
        this.ants = ants;
    }
}
