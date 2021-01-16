package ir.sharif.aichallenge.server.common.network.data;

import lombok.Getter;

@Getter

public class RangeUpgradeInfo extends UpgradeInfo {

    public RangeUpgradeInfo(int unitId) {
        super(unitId);
    }

    public String getType() {
        return MessageTypes.UPGRADE_RANGE;
    }
}
