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
