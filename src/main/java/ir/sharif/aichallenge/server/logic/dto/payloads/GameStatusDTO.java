package ir.sharif.aichallenge.server.logic.dto.payloads;

import ir.sharif.aichallenge.server.logic.handlers.AttackSummary;
import ir.sharif.aichallenge.server.logic.model.Game;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            this.current_resource_type = currentAnt.getCarryingResourceType().getValue();
            this.health = currentAnt.getHealth();
            this.around_cells = Arrays.stream(game.getMap().getAntViewableCells(current_x, current_y))
                    .map(x -> new AroundCell(x, currentAnt)).toArray(AroundCell[]::new);
            this.chat_box = game.getColony(currentAnt.getColonyId()).getChatBox().getChatMessages().stream()
                    .map(ChatBoxMessageDTO::new).toArray(ChatBoxMessageDTO[]::new);
        }
    }

    private List<AttackDTO> getNearByAttacks(Game game, Integer ant_id) {
        Ant ant = game.getAntByID(ant_id);
        List<AttackSummary> attackSummaries = game.getAttackHandler().getNearByAttacks(ant_id);
        return attackSummaries.stream()
                .map(x -> new AttackDTO(x.src_row, x.src_col,
                        x.dst_col, x.dst_row,
                        isAttackerEnemy(game, ant, x)))
                .collect(Collectors.toList());
    }

    private boolean isAttackerEnemy(Game game, Ant ant, AttackSummary x) {
        if (x.attacker_id < 0) {
            return x.attacker_id != game.getColony(ant.getColonyId()).getBaseAttackerId();
        }
        return game.getAntRepository().getAliveOrDeadAnt(x.attacker_id).getColonyId() != ant.getColonyId();
    }
}
