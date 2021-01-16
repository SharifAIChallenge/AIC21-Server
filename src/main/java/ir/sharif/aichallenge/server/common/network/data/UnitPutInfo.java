package ir.sharif.aichallenge.server.common.network.data;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class UnitPutInfo extends ClientMessageInfo {
    private final int typeId;
    private final int pathId;

    public UnitPutInfo(int typeId, int pathId) {
        this.typeId = typeId;
        this.pathId = pathId;
    }

    @Override
    public String getType() {
        return MessageTypes.PUT_UNIT;
    }
}
