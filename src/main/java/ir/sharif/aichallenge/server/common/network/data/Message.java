package ir.sharif.aichallenge.server.common.network.data;

import com.google.gson.JsonObject;
import lombok.Getter;

/**
 * Message is the data that is transferred in network between clients and server
 */
@Getter
public class Message {

    private final String type;
    // payload = info
    private final JsonObject info;

    public Message(String type, JsonObject info) {
        this.type = type;
        this.info = info;
    }

    public String getType() {
        return type;
    }

    public JsonObject getInfo() {
        return info;
    }
}
