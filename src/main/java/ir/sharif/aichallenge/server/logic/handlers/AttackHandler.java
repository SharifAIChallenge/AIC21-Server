package ir.sharif.aichallenge.server.logic.handlers;

import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.Colony;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;

import java.util.HashMap;

public class AttackHandler {
    private HashMap<Integer, Colony> colonyHashMap;
    private GameMap map;

    public AttackHandler(GameMap map, HashMap<Integer, Colony> colonyHashMap) {
        this.map = map;
        this.colonyHashMap = colonyHashMap;
    }

    public void runAttack(Ant ant) {
        Cell[] cells = map.getAttackableCells(ant.getXPosition(), ant.getYPosition());
        for (Cell cell : cells) {
            if(cell.isBase() && colonyHashMap.get(ant.getColonyId()).getBase() != cell){

            }
        }
    }
}
