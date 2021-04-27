package ir.sharif.aichallenge.server.logic.model.cell;

public enum CellType {
    WALL, EMPTY, BASE, TRAP, SWAMP;

    public int getValue() {
        switch (this) {
        case WALL:
            return 2;
        case EMPTY:
            return 1;
        case BASE:
            return 0;
        case TRAP: // 4 in map
            return 3;
        case SWAMP: // 5 in map
            return 4;
        default:
            return 1;
        }
    }

    public static CellType getCellType(int value) {
        switch (value) {
        case 0:
            return BASE;
        case 1:
            return EMPTY;
        case 2:
            return WALL;
        case 4:
            return SWAMP;
        case 3:
            return TRAP;
        default:
            return EMPTY;
        }
    }

    @Override
    public String toString() {
        switch (this) {
        case WALL:
            return "wall";
        case EMPTY:
            return "empty";
        case BASE:
            return "base";
        case SWAMP:
            return "swamp";
        case TRAP:
            return "trap";
        default:
            return "empty";
        }
    }
}
