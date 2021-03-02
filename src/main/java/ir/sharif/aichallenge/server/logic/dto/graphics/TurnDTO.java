package ir.sharif.aichallenge.server.logic.dto.graphics;

import java.util.List;

public class TurnDTO {
    public int turn_num;
    public List<String> chat_box_0;
    public List<String> chat_box_1;
    public int base0_health;
    public int base1_health;
    public List<CellDTO> cells;
    public List<AttackDTO> attacks;
}
