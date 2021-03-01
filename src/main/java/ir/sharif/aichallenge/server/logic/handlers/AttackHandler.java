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

    public AttackHandler(GameMap map, AntRepository antRepository) {
        this.map = map;
        this.antRepository = antRepository;
        rand = new Random();
    }

    public void handleAttacks() {
        for (Colony colony : antRepository.getColonies()) {
            runAttack(colony.getId(), colony.getBase().getX(), colony.getBase().getY());
        }

        for (Colony colony : antRepository.getColonies()) {
            for (Ant ant : colony.getAnts()) {
                if (ant.getAntType().equals(AntType.SOLDIER))
                    runAttack(colony.getId(), ant.getXPosition(), ant.getYPosition());
            }
        }
        handleDeadAnts();
    }

    private void runAttack(int colonyId, int fromXPosition, int fromYPosition) {
        Cell[] cells = map.getAttackableCells(fromXPosition, fromYPosition);
        List<Ant> workers = new ArrayList<>();
        List<Ant> soldiers = new ArrayList<>();

        for (Cell cell : cells) {
            if (cell.isBase() && colonyId != ((BaseCell) cell).getColony().getId()) {
                ((BaseCell) cell).getColony().decreaseBaseHealth(1);
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
            int index = rand.nextInt(soldiers.size());
            soldiers.get(index).decreaseHealth(1);
            return;
        }

        if (workers.size() > 0) {
            int index = rand.nextInt(workers.size());
            workers.get(index).decreaseHealth(1);
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
                map.addResource(ResourceType.GRASS, ConstConfigs.RATE_DEATH_RESOURCE, ant.getXPosition(),
                        ant.getYPosition());
            } else {
                if (ant.getCarryingResourceType() == ResourceType.NONE)
                    map.addResource(ResourceType.BREAD, ConstConfigs.RATE_DEATH_RESOURCE, ant.getXPosition(),
                            ant.getYPosition());
                else if (ant.getCarryingResourceType() == ResourceType.BREAD)
                    map.addResource(ResourceType.BREAD,
                            ConstConfigs.RATE_DEATH_RESOURCE + ant.getCarryingResourceAmount(), ant.getXPosition(),
                            ant.getYPosition());
                else {
                    map.addResource(ResourceType.BREAD, ConstConfigs.RATE_DEATH_RESOURCE, ant.getXPosition(),
                            ant.getYPosition());
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
}
