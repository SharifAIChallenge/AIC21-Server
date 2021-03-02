package ir.sharif.aichallenge.server.logic.dto.graphics;

import ir.sharif.aichallenge.server.logic.model.ant.Ant;

public class AntDTO {
    public int id;
    public int team;
    public int type;
    public int health;
    public int resource;

    public AntDTO(Ant ant) {
        this.id = ant.getId();
        this.team = ant.getColonyId();
        this.type = ant.getAntType().getValue();
        this.health = ant.getHealth();
        this.resource = ant.getCarryingResourceType().getValue();
    }
}
