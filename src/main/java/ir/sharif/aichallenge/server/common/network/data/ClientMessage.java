package ir.sharif.aichallenge.server.common.network.data;

import com.google.gson.JsonObject;
import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.common.util.Log;
import lombok.Getter;

import java.lang.reflect.Type;

@Getter
public class ClientMessage extends Message {

    private final int turn;

    public int getTurn() {
        return turn;
    }

    public ClientMessage(String type, JsonObject info, int turn) {
        super(type, info);
        this.turn = turn;
    }

    public ClientMessageInfo getParsedInfo() {
       Class clazz;
       switch (this.getType()) {
           case MessageTypes.ACTION:
               clazz = ActionInfo.class;
               break;
           case MessageTypes.SEND_MESSAGE:
               clazz = SendMessageInfo.class;
               break;
           default:
               Log.e("ClientMessage", "Invalid message type: " + this.getType());
               return null;
       }
       return (ClientMessageInfo) Json.GSON.fromJson(this.getInfo(), clazz);
    }
}
