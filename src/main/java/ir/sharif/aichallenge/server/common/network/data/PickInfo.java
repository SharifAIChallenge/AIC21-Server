package ir.sharif.aichallenge.server.common.network.data;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class PickInfo extends ClientMessageInfo {
    private final List<Integer> units;

    public PickInfo(List<Integer> units) {
        this.units = units;
    }

    public PickInfo(Integer... unitTypes) {
        this.units = Arrays.asList(unitTypes);
    }

    @Override
    public String getType() {
        return MessageTypes.PICK;
    }
}
