package ir.sharif.aichallenge.server.common.network.data;


import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class SendMessageInfo extends ClientMessageInfo {
    private final String message;
    private final int value;

    public SendMessageInfo(int value, String message) {
        this.message = message;
        this.value = value;
    }

    @Override
    public String getType() {
        return MessageTypes.SEND_MESSAGE;
    }

    public String getMessage() {
        return message;
    }

    public int getValue() {
        return value;
    }
}

