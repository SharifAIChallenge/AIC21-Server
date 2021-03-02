package ir.sharif.aichallenge.server.logic.dto.graphics;

public class AttackDTO {
    public int attacker_id;
    public int defender_id;
    public int src_row;
    public int src_col;
    public int dst_row;
    public int dst_col;

    public AttackDTO(int attacker_id, int defender_id, int src_row, int src_col, int dst_row, int dst_col) {
        this.attacker_id = attacker_id;
        this.defender_id = defender_id;
        this.src_row = src_row;
        this.src_col = src_col;
        this.dst_row = dst_row;
        this.dst_col = dst_col;
    }
}
