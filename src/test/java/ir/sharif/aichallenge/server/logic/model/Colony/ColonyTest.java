package ir.sharif.aichallenge.server.logic.model.Colony;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.cell.BaseCell;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class ColonyTest {
    private Colony colony;
    private BaseCell baseCell;

    @BeforeEach
    void setUp() {
        baseCell = new BaseCell(0, 0);
        colony = new Colony(0, baseCell, 5);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addNewAnt() {
        Ant soldier = new Ant(0, colony.getId(), 0, 0, AntType.SOLDIER);
        Ant worker = new Ant(1, colony.getId(), 0, 0, AntType.WORKER);
        try {
            colony.addNewAnt(worker);
            colony.addNewAnt(soldier);
        } catch (GameActionException e) {
            fail(e);
            e.printStackTrace();
        }

        assertEquals(2, colony.getAllAntsGeneratedCount());
        assertEquals(1, colony.getAllSoldierAntsGeneratedCount());
        assertEquals(soldier, colony.getAnt(0));
        assertEquals(worker, colony.getAnt(1));
    }

    @Test
    void addResource() {
        assertEquals(0, colony.getAllResourcesAmount());

        colony.addResource(ResourceType.BREAD, ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT - 1);
        assertEquals(ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT - 1, colony.getGainedBread());
        assertEquals(1, colony.getToBeGeneratedWorkersCount());
    }

    @Test
    void addEnoughResourceToMakeWorker(){
        assertEquals(0, colony.getAllResourcesAmount());

        colony.addResource(ResourceType.BREAD, ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT);
        assertEquals(0, colony.getGainedBread());
        assertEquals(2, colony.getToBeGeneratedWorkersCount());
    }

    @Test
    void addEnoughResourceToMakeWorker2(){
        assertEquals(0, colony.getAllResourcesAmount());

        colony.addResource(ResourceType.BREAD, ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT * 3 + 1);
        assertEquals(1, colony.getGainedBread());
        assertEquals(4, colony.getToBeGeneratedWorkersCount());
    }

}