package ir.sharif.aichallenge.server.logic.dto.graphics;

import ir.sharif.aichallenge.server.engine.config.Configs;
import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;

public class GraphicGameConfigDTO {
    public int map_height;
    public int map_width;
    public CellTypeDTO[] cells_type;
    public int base_health;
    public int worker_health;
    public int soldier_health;
    public String team0_name = Configs.FIRST_TEAM_NAME;
    public String team1_name = Configs.SECOND_TEAM_NAME;
    public int winner;

    public GraphicGameConfigDTO(GameMap map) {
        this.map_height = map.getYAxisLength();
        this.map_width = map.getXAxisLength();
        this.base_health = ConstConfigs.BASE_INIT_HEALTH;
        this.worker_health = ConstConfigs.WORKER_ANT_INITIAL_HEALTH;
        this.soldier_health = ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH;
        this.cells_type = new CellTypeDTO[map.getXAxisLength() * map.getYAxisLength()];
        int index = 0;
        for (int x = 0; x < map.getXAxisLength(); x++) {
            for (int y = 0; y < map.getYAxisLength(); y++) {
                this.cells_type[index++] = new CellTypeDTO(y, x, map.getCell(x, y).getCellType());
            }
        }
    }
}