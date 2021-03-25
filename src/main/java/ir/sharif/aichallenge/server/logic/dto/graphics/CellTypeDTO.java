package ir.sharif.aichallenge.server.logic.dto.graphics;

import ir.sharif.aichallenge.server.logic.model.cell.CellType;

public class CellTypeDTO {
    public static boolean hasPassed = false;
    public int row;
    public int col;
    public int cell_type;

    public CellTypeDTO(int row, int col, CellType cell_type) {
        this.row = row;
        this.col = col;
        if (cell_type == CellType.BASE) {
            if (hasPassed) {
                this.cell_type = 1;
            } else {
                this.cell_type = 0;
                hasPassed = true;
            }
        } else
            this.cell_type = (cell_type == CellType.BASE) ? 0 : cell_type.getValue() + 1;
    }
}
