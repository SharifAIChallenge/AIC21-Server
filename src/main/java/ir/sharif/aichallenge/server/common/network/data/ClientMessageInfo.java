package ir.sharif.aichallenge.server.common.network.data;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

public abstract class ClientMessageInfo {
    @Expose
    @Getter
    @Setter
    private int playerId;

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public abstract String getType();
}
