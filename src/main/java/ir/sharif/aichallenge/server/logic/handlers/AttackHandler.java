package ir.sharif.aichallenge.server.logic.handlers;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.AntRepository;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.cell.BaseCell;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.Colony.Colony;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AttackHandler {
    private AntRepository antRepository;
    private GameMap map;
    private Random rand;
    private HashMap<Integer, Ant> newDeadAnts;
    private List<AttackSummary> attackSummaries;

    public AttackHandler(GameMap map, AntRepository antRepository) {
        this.map = map;
        this.antRepository = antRepository;
        rand = new Random();
    }

    public void handleAttacks() {
        attackSummaries = new ArrayList<>();

        for (Colony colony : antRepository.getColonies()) {
            runAttack(colony.getId(), colony.getBase().getX(), colony.getBase().getY(),
                    ConstConfigs.BASE_ATTACK_DAMAGE, ConstConfigs.BASE_MAX_ATTACK_DISTANCE, -1);
        }

        for (Colony colony : antRepository.getColonies()) {
            for (Ant ant : colony.getAnts()) {
                if (ant.getAntType().equals(AntType.SOLDIER))
                    runAttack(colony.getId(), ant.getXPosition(), ant.getYPosition(),
                            ConstConfigs.ANT_ATTACK_DAMAGE, ConstConfigs.ANT_MAX_ATTACK_DISTANCE, ant.getId());
            }
        }
        handleDeadAnts();
    }

    private void runAttack(int colonyId, int fromXPosition, int fromYPosition, int damage, int maxDistance, int attackerId) {
        Cell[] cells = map.getAttackableCells(fromXPosition, fromYPosition, maxDistance);
        List<Ant> workers = new ArrayList<>();
        List<Ant> soldiers = new ArrayList<>();

        for (Cell cell : cells) {
            if (cell.isBase() && colonyId != ((BaseCell) cell).getColony().getId()) {
                ((BaseCell) cell).getColony().decreaseBaseHealth(damage);
                return;
            }
            for (Ant cellAnt : cell.getAnts()) {
                if (cellAnt.getColonyId() != colonyId && cellAnt.getHealth() > 0) {
                    if (cellAnt.getAntType() == AntType.SOLDIER)
                        soldiers.add(cellAnt);
                    else
                        workers.add(cellAnt);
                }
            }
        }

        if (soldiers.size() > 0) {
            runAttack(fromXPosition, fromYPosition, damage, attackerId, soldiers);
            return;
        }

        if (workers.size() > 0) {
            runAttack(fromXPosition, fromYPosition, damage, attackerId, workers);
        }
    }

    private void runAttack(int fromXPosition, int fromYPosition, int damage, int attackerId, List<Ant> ants) {
        // attackerId = -1 --> base attack
        int index = rand.nextInt(ants.size());
        Ant defender = ants.get(index);
        defender.decreaseHealth(damage);
        if (attackerId != -1) {
            AttackSummary attackSummary = new AttackSummary(attackerId, defender.getId(), fromYPosition, fromXPosition, defender.getYPosition(), defender.getXPosition());
            attackSummaries.add(attackSummary);
        }
    }

    private void handleDeadAnts() {
        newDeadAnts = new HashMap<>();
        ArrayList<Integer> deadAntIDs = new ArrayList<>();
        for (Ant ant : antRepository.getAllAnts()) {
            if (!ant.isDead())
                continue;
            antRepository.getColony(ant.getColonyId()).removeAnt(ant.getId());
            map.getCell(ant.getXPosition(), ant.getYPosition()).removeAnt(ant);
            // antRepository.removeDeadAnt(ant.getId());
            deadAntIDs.add(ant.getId());
            newDeadAnts.put(ant.getId(), ant);
            if (ant.getAntType() == AntType.SOLDIER) {
                map.addResource(ResourceType.GRASS,
                        (int) Math.ceil(ConstConfigs.RATE_DEATH_RESOURCE * ConstConfigs.GENERATE_SOLDIER_GRASS_AMOUNT),
                        ant.getXPosition(), ant.getYPosition());
            } else {
                if (ant.getCarryingResourceType() == ResourceType.NONE)
                    map.addResource(ResourceType.BREAD,
                            (int) Math.ceil(ConstConfigs.RATE_DEATH_RESOURCE * ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT),
                            ant.getXPosition(), ant.getYPosition());
                else if (ant.getCarryingResourceType() == ResourceType.BREAD)
                    map.addResource(ResourceType.BREAD,
                            (int) Math.ceil(ConstConfigs.RATE_DEATH_RESOURCE * ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT
                                    + ant.getCarryingResourceAmount()),
                            ant.getXPosition(), ant.getYPosition());
                else {
                    map.addResource(ResourceType.BREAD,
                            (int)Math.ceil(ConstConfigs.RATE_DEATH_RESOURCE * ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT),
                            ant.getXPosition(), ant.getYPosition());
                    map.addResource(ResourceType.GRASS, ant.getCarryingResourceAmount(), ant.getXPosition(),
                            ant.getYPosition());
                }
            }
        }
        for (Integer antId : deadAntIDs) {
            antRepository.removeDeadAnt(antId);
        }

    }

    public HashMap<Integer, Ant> getNewDeadAnts() {
        return newDeadAnts;
    }

    public List<AttackSummary> getAttackSummaries() {
        return attackSummaries;
    }
}
