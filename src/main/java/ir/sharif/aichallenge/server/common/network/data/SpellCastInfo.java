package ir.sharif.aichallenge.server.common.network.data;

import ir.sharif.aichallenge.server.logic.map.Cell;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SpellCastInfo extends ClientMessageInfo {

    private int typeId;
    private Cell cell;
    private int unitId;
    private int pathId;

    @Override
    public String getType() {
        return MessageTypes.CAST_SPELL;
    }
}
