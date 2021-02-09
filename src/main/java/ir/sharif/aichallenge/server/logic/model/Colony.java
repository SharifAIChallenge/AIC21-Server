package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;

import java.util.HashMap;

public class Colony {
    private int id;
    private int gainedBread = 0;
    private int gainedGrass = 0;
    private Cell base;
    private int baseHealth;
    private HashMap<Integer, Ant> ants;
    private ChatBox chatBox;

    public Colony(int id, Cell base, int baseHealth) {
        this.id = id;
        addBread(ConstConfigs.COLONY_INITIAL_BREAD);
        addGrass(ConstConfigs.COLONY_INITIAL_GRASS);
        this.base = base;
        this.baseHealth = baseHealth;
        ants = new HashMap<>();
    }

    public void addBread(int amount) {
        gainedBread += amount;
        if (gainedBread >= ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT) {
            generateWorker();
            gainedBread -= ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT;
        }
    }

    public void addGrass(int amount) {
        gainedGrass += amount;
        if (gainedGrass >= ConstConfigs.GENERATE_SOLDIER_GRASS_AMOUNT) {
            generateSoldier();
            gainedGrass -= ConstConfigs.GENERATE_SOLDIER_GRASS_AMOUNT;
        }
    }

    private void generateSoldier() {
    }

    private void generateWorker() {
    }

    public Ant getAnt(int antId){
        return ants.get(antId);
    }

    public ChatBox getChatBox() {
        return chatBox;
    }
}
