package ir.sharif.aichallenge.server.logic.model.cell;

import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {
    private Cell baseCell;
    private Cell wallCell;
    private Cell emptyCell;
    private Cell breadEmptyCell;
    private Cell grassEmptyCell;

    private Ant soldierAnt;
    private Ant workerAnt;

    @BeforeEach
    void setUp() {
        baseCell = new Cell(0, 0, CellType.BASE, ResourceType.NONE, -1);
        wallCell = new Cell(0, 1, CellType.WALL, ResourceType.NONE, -1);
        emptyCell = new Cell(0, 2, CellType.EMPTY, ResourceType.NONE, -1);
        breadEmptyCell = new Cell(0, 3, CellType.EMPTY, ResourceType.BREAD, 1);
        grassEmptyCell = new Cell(0, 4, CellType.EMPTY, ResourceType.GRASS, 1);
        soldierAnt = new Ant(0, 0, 0, 0, AntType.SOLDIER);
        workerAnt = new Ant(1, 0, 0, 0, AntType.WORKER);
        baseCell.addAnt(soldierAnt);
        baseCell.addAnt(workerAnt);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void isBase() {
        assertTrue(baseCell.isBase());
        assertFalse(wallCell.isBase());
        assertFalse(emptyCell.isBase());
        assertFalse(breadEmptyCell.isBase());
        assertFalse(grassEmptyCell.isBase());
    }

    @Test
    void getWorkerAnts() {
        List<Ant> workerAnts = baseCell.getWorkerAnts();
        assertEquals(1, workerAnts.size());
        assertEquals(workerAnt, workerAnts.get(0));
        assertFalse(workerAnts.contains(soldierAnt));
    }

    @Test
    void getEmptyWorkerAnts() {
        List<Ant> workerAnts = wallCell.getWorkerAnts();
        assertEquals(0, workerAnts.size());
    }

    //TODO add more tests with different states for manageResources
    @Test
    void manageResources() {
        breadEmptyCell.addAnt(workerAnt);
        breadEmptyCell.addAnt(soldierAnt);
        breadEmptyCell.manageResources();

        assertEquals(ResourceType.NONE, breadEmptyCell.getResourceType());
        assertEquals(-1, breadEmptyCell.getResourceAmount());
        assertEquals(ResourceType.BREAD, workerAnt.getCarryingResourceType());
        assertEquals(1, workerAnt.getCarryingResourceAmount());
    }

    @Test
    void manageResourcesWithAntsMoreThanResource() {
        Ant newWorkerAnt = new Ant(2, 0, 0, 0, AntType.WORKER);
        breadEmptyCell.addAnt(workerAnt);
        breadEmptyCell.addAnt(newWorkerAnt);
        breadEmptyCell.addAnt(soldierAnt);
        breadEmptyCell.manageResources();

        assertEquals(ResourceType.NONE, breadEmptyCell.getResourceType());
        assertEquals(-1, breadEmptyCell.getResourceAmount());

        if (newWorkerAnt.getCarryingResourceType() == ResourceType.BREAD){
            assertEquals(ResourceType.NONE, workerAnt.getCarryingResourceType());
            assertEquals(0, workerAnt.getCarryingResourceAmount());

            assertEquals(1, newWorkerAnt.getCarryingResourceAmount());
        }else {
            assertEquals(ResourceType.NONE, newWorkerAnt.getCarryingResourceType());
            assertEquals(0, newWorkerAnt.getCarryingResourceAmount());

            assertEquals(1, workerAnt.getCarryingResourceAmount());
            assertEquals(ResourceType.BREAD, workerAnt.getCarryingResourceType());
        }
    }

    @Test
    void getResourceAmount() {
        assertEquals(1, breadEmptyCell.getResourceAmount());
        assertEquals(-1, baseCell.getResourceAmount());
    }

}