package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;

public class SoldierAnt extends Ant {
    public SoldierAnt() {
        health = ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH;
    }
}
