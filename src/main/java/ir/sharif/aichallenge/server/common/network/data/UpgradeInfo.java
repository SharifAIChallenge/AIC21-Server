package ir.sharif.aichallenge.server.common.network.data;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class UpgradeInfo extends ClientMessageInfo {
    private final int unitId;

    public UpgradeInfo(int unitId) {
        this.unitId = unitId;
    }
}
