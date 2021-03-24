package ir.sharif.aichallenge.server.logic.dto.graphics;

import java.util.List;

public class TurnDTO {
    public int turn_num;
    public int base0_health;
    public int base1_health;
    public List<CellDTO> cells;
    public List<AttackDTO> attacks;

    public int team0_alive_workers = 10;
    public int team0_total_workers = 100;
    public int team0_alive_soldiers = 5;
    public int team0_total_soldiers = 40;
    public int team1_alive_workers = 10;
    public int team1_total_workers = 100;
    public int team1_alive_soldiers = 5;
    public int team1_total_soldiers = 40;

    public int team0_current_resource0 = 10;
    public int team0_total_resource0 = 100;
    public int team0_current_resource1 = 5;
    public int team0_total_resource1 = 40;

    public int team1_current_resource0 = 10;
    public int team1_total_resource0 = 100;
    public int team1_current_resource1 = 5;
    public int team1_total_resource1 = 40;

    public List<ChatElementDTO> trivial_chat_box_0;
    public List<ChatElementDTO> trivial_chat_box_1;
    public List<ChatElementDTO> important_chat_box_0;
    public List<ChatElementDTO> important_chat_box_1;

}
