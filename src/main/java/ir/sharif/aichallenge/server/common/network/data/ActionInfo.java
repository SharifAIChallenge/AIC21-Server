package ir.sharif.aichallenge.server.common.network.data;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class ActionInfo extends ClientMessageInfo {
    private final int direction;

    public ActionInfo(int direction) {
        this.direction = direction;
    }

    @Override
    public String getType() {
        return MessageTypes.ACTION;
    }
}
