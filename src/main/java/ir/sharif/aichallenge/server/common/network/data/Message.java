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
    private final JsonObject payload;

    public Message(String type, JsonObject payload, String token) {
        this.type = type;
        this.payload = payload;
    }

    // for server messages
    public Message(String type, JsonObject payload) {
        this.type = type;
        this.payload = payload;
    }

    public String getType() {
        return type;
    }

    public JsonObject getInfo() {
        return payload;
    }
}
