package ir.sharif.aichallenge.server.logic.dto.graphics;

import java.util.ArrayList;
import java.util.List;

import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;

public class CellDTO {
    public int row;
    public int col;
    public int resource_value;
    public int resource_type;
    public List<AntDTO> ants;

    public CellDTO(Cell cell) {
        this.row = cell.getY();
        this.col = cell.getX();
        this.resource_type = cell.getResourceType().getValue();
        this.resource_value = cell.getResourceAmount();
        this.ants = new ArrayList<>();
        for (Ant ant : cell.getAnts()) {
            this.ants.add(new AntDTO(ant));
        }
    }
}
