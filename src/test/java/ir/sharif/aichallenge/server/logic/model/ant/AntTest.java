package ir.sharif.aichallenge.server.logic.model.ant;

import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AntTest {
    private Ant soldierAnt;
    private Ant workerAnt;

    @BeforeEach
    void setUp() {
        soldierAnt = new Ant(0, 0, 0, 0, AntType.SOLDIER);
        workerAnt = new Ant(1, 0, 0, 0, AntType.WORKER);
    }

    @Test
    void moveTo() {
        soldierAnt.moveTo(2, 3);
        assertEquals(2, soldierAnt.getXPosition());
        assertEquals(3, soldierAnt.getYPosition());
    }

    @Test
    void isDead() {
        workerAnt.decreaseHealth(workerAnt.getHealth());
        assertTrue(workerAnt.isDead());

        if (soldierAnt.getHealth() > 0) {
            soldierAnt.decreaseHealth(soldierAnt.getHealth() - 1);
            assertFalse(soldierAnt.isDead());
        } else {
            assertTrue(soldierAnt.isDead());
        }
    }

    @Test
    void getAntType() {
        assertEquals(AntType.WORKER, workerAnt.getAntType());
        assertEquals(AntType.SOLDIER, soldierAnt.getAntType());
    }

    @Test
    void getCarryingResourceType() {
        assertEquals(ResourceType.NONE, soldierAnt.getCarryingResourceType());

        ResourceType expectedResourceType = ResourceType.BREAD;
        workerAnt.setCarryingResourceType(expectedResourceType);

        assertEquals(expectedResourceType, workerAnt.getCarryingResourceType());
    }
}