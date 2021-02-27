package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.logic.handlers.exceptions.ColonyNotExistsException;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.model.Colony.Colony;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;

import java.util.Collection;
import java.util.HashMap;

public class AntRepository {
    // maps colony ids to colony
    private HashMap<Integer, Colony> colonyHashMap;
    // contains alive ants and maps ant ids to ant
    private HashMap<Integer, Ant> antHashMap;

    public AntRepository(HashMap<Integer, Colony> colonyHashMap) {
        this.colonyHashMap = colonyHashMap;
        initAntHashMap();
    }

    private void initAntHashMap() {
        antHashMap = new HashMap<>();
        for (Colony colony : colonyHashMap.values()) {
            for (Ant ant : colony.getAnts()) {
                antHashMap.put(ant.getId(), ant);
            }
        }
    }

    public Colony getColony(int colonyId) {
        return colonyHashMap.get(colonyId);
    }

    public Ant getAnt(int antId) {
        return antHashMap.getOrDefault(antId, null);
    }

    public Collection<Colony> getColonies() {
        return colonyHashMap.values();
    }

    public boolean doesAntExists(int antId) {
        return antHashMap.containsKey(antId);
    }

    public void addAnt(Ant ant, Integer colonyId) throws GameActionException {
        Colony colony = this.getColony(colonyId);
        if (colony == null) {
            throw new ColonyNotExistsException("", colonyId);
        }
        colony.addNewAnt(ant);
        antHashMap.put(ant.getId(), ant);
    }

    public Collection<Ant> getAllAnts() {
        return antHashMap.values();
    }

    public void removeDeadAnt(int antId) {
        antHashMap.remove(antId);
    }
}
