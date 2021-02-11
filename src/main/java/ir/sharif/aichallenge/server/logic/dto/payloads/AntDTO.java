package ir.sharif.aichallenge.server.logic.dto.payloads;

import ir.sharif.aichallenge.server.logic.model.ant.Ant;

public class AntDTO {
    int ant_team;
    int ant_type;

    public AntDTO(Ant ant, Ant currentAnt) {
        this.ant_team = (ant.getColonyId() != currentAnt.getColonyId()) ? 1 : 0;
        this.ant_type = ant.getAntType().getValue();
    }
}
