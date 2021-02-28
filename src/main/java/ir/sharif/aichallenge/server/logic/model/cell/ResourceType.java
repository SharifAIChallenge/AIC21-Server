package ir.sharif.aichallenge.server.logic.model.cell;

public enum ResourceType {
    NONE, BREAD, GRASS;

    public int getValue() {
        switch (this) {
            case NONE:
                return 2;
            case BREAD:
                return 0;
            case GRASS:
                return 1;
            default:
                return 2;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case NONE:
                return " ";
            case BREAD:
                return "bread";
            case GRASS:
                return "grass";
            default:
                return "";
        }
    }

}
