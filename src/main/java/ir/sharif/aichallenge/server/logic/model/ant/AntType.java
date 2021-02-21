package ir.sharif.aichallenge.server.logic.model.ant;

public enum AntType {
    SOLDIER, WORKER;

    public int getValue() {
        switch (this) {
            case WORKER:
                return 1;
            case SOLDIER:
                return 0;
            default:
                return 1;
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case WORKER:
                return "worker";
            case SOLDIER:
                return "solder";
            default:
                return "none";
        }
    }
}
