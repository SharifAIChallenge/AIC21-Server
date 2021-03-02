package ir.sharif.aichallenge.server.logic.model.map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameMapTest {

    private GameMap gameMap;
    @BeforeEach
    void setUp() {
        MapGenerator.MapGeneratorResult generatedMap = MapGenerator.generateRandomMap();
        gameMap = generatedMap.map;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getCell() {
    }

    @Test
    void getViewableCells() {
    }

    @Test
    void getAttackableCells() {
    }

    @Test
    void addResource() {
    }

    @Test
    void getAllCells() {
    }

    @Test
    void changeAntCurrentCell() {
    }
}