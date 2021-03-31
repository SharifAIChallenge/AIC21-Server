package ir.sharif.aichallenge.server.logic.dto.payloads;

public class AttackDTO {
    public int attacker_row; // row --> y, col --> x
    public int attacker_col;
    public int defender_row;
    public int defender_col;
    public boolean is_attacker_enemy;

    public AttackDTO(int attacker_row, int attacker_col, int defender_col, int defender_row,
            boolean is_attacker_enemy) {
        this.attacker_row = attacker_row;
        this.attacker_col = attacker_col;
        this.defender_col = defender_col;
        this.defender_row = defender_row;
        this.is_attacker_enemy = is_attacker_enemy;
    }
}
