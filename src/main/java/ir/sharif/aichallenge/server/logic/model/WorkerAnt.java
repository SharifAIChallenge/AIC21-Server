package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;

public class WorkerAnt extends Ant {
    public WorkerAnt() {
        health = ConstConfigs.WORKER_ANT_INITIAL_HEALTH;
    }
}
