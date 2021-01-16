package ir.sharif.aichallenge.server.common.network.data;

import lombok.Getter;

@Getter
public class DamageUpgradeInfo extends UpgradeInfo {
    public DamageUpgradeInfo(int unitId) {
        super(unitId);
    }

    @Override
    public String getType() {
        return MessageTypes.UPGRADE_DAMAGE;
    }
}
