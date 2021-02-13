package ir.sharif.aichallenge.server.logic.handlers;

import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.cell.BaseCell;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.Colony;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AttackHandler {
    private HashMap<Integer, Colony> colonyHashMap;
    private GameMap map;
    private Random rand;

    public AttackHandler(GameMap map, HashMap<Integer, Colony> colonyHashMap) {
        this.map = map;
        this.colonyHashMap = colonyHashMap;
        rand = new Random();
    }

    public void runAttack(Ant ant) {
        Cell[] cells = map.getAttackableCells(ant.getXPosition(), ant.getYPosition());
        List<Ant> workers = new ArrayList<>();
        List<Ant> soldiers = new ArrayList<>();

        for (Cell cell : cells) {
            if (cell.isBase() && ant.getColonyId() != ((BaseCell) cell).getColony().getId()) {
                ((BaseCell) cell).getColony().decreaseBaseHealth(1);
                return;
            }
            for (Ant cellAnt : cell.getAnts()) {
                if (cellAnt.getColonyId() != ant.getColonyId() && cellAnt.getHealth() > 0) {
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
}
