package ir.sharif.aichallenge.server.logic.dto.payloads;

import ir.sharif.aichallenge.server.logic.model.Game;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;

import java.util.Arrays;

public class GameStatusDTO {
    AroundCell[] around_cells;
    ChatBoxMessageDTO[] chat_box;
    int current_x;
    int current_y;
    int current_resource_value;
    int current_resource_type;
    int health;

    public GameStatusDTO(Game game, Integer antID) {
        Ant currentAnt = game.getAntByID(antID);
        if (currentAnt != null) {
            this.current_x = currentAnt.getXPosition();
            this.current_y = currentAnt.getYPosition();
            this.current_resource_value = currentAnt.getCarryingResourceAmount();
            this.current_resource_type = currentAnt.getAntType().getValue();
            this.health = currentAnt.getHealth();
            this.around_cells = Arrays.stream(game.getMap().getAntViewableCells(current_x, current_y))
                    .map(x -> new AroundCell(x, currentAnt)).toArray(AroundCell[]::new);
            this.chat_box = game.getColony(currentAnt.getColonyId()).getChatBox().getChatMessages().stream()
                    .map(ChatBoxMessageDTO::new).toArray(ChatBoxMessageDTO[]::new);
        }
    }
}
