package ir.sharif.aichallenge.server.common.network.data;

import com.google.gson.JsonObject;
import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.utils.Log;
import lombok.Getter;

import java.lang.reflect.Type;

@Getter
public class ClientMessage extends Message {

    private final int turn;

    public ClientMessage(String type, JsonObject info, int turn) {
        super(type, info);
        this.turn = turn;
    }

    public ClientMessageInfo getParsedInfo() {
        Class clazz;
        switch (this.getType()) {
            case MessageTypes.PUT_UNIT:
                clazz = UnitPutInfo.class;
                break;
            case MessageTypes.CAST_SPELL:
                clazz = SpellCastInfo.class;
                break;
            case MessageTypes.UPGRADE_DAMAGE:
                clazz = DamageUpgradeInfo.class;
                break;
            case MessageTypes.UPGRADE_RANGE:
                clazz = RangeUpgradeInfo.class;
                break;
            case MessageTypes.PICK:
                clazz = PickInfo.class;
                break;
            default:
                Log.e("ClientMessage", "Invalid message type: " + this.getType());
                return null;
        }
        return (ClientMessageInfo) Json.GSON.fromJson(this.getInfo(), clazz);
    }
}
