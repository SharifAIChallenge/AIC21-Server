package ir.sharif.aichallenge.server.logic.model.cell;

public enum CellType {
    WALL,
    EMPTY,
    BASE;

    public int getValue() {
        switch (this) {
            case WALL:
                return 2;
            case EMPTY:
                return 1;
            case BASE:
                return 0;
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
            default:
                return "empty";
        }
    }
}
