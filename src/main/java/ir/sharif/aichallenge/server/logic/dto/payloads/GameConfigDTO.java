package ir.sharif.aichallenge.server.logic.dto.payloads;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.Game;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;

public class GameConfigDTO {
    int map_width;
    int map_height;
    int ant_type;
    int base_x;
    int base_y;
    int health_kargar;
    int health_sarbaaz;
    int attack_distance;
    int generate_kargar;
    int generate_sarbaaz;
    int rate_death_resource;

    public GameConfigDTO(Game game, int antId) {
        Ant ant = game.getAntByID(antId);
        this.map_width = game.getMap().getXAxisLength();
        this.map_height = game.getMap().getYAxisLength();
        this.ant_type = ant.getAntType().getValue();
        this.base_x = game.getColony(ant.getColonyId()).getBase().getX();
        this.base_y = game.getColony(ant.getColonyId()).getBase().getY();
        this.health_kargar = ConstConfigs.WORKER_ANT_INITIAL_HEALTH;
        this.health_sarbaaz = ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH;
        this.attack_distance = ConstConfigs.ANT_MAX_ATTACK_DISTANCE;
        this.generate_kargar = ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT;
        this.generate_sarbaaz = ConstConfigs.GENERATE_SOLDIER_GRASS_AMOUNT;
        this.rate_death_resource = ConstConfigs.RATE_DEATH_RESOURCE;
    }
}
