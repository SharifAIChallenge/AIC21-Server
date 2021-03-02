package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.logic.model.Colony.Colony;
import ir.sharif.aichallenge.server.logic.model.map.MapGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameJudgeTest {
    private GameJudge gameJudge;
    private AntRepository antRepository;

    @BeforeEach
    void setUp() {
        MapGenerator.MapGeneratorResult generatedMap = MapGenerator.generateRandomMap();
        antRepository = new AntRepository(generatedMap.colonies);
        gameJudge = new GameJudge(antRepository);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getWinner() {
        antRepository.getColony(0).decreaseBaseHealth(1);
        Colony winner = gameJudge.getWinner();
        assertEquals(antRepository.getColony(1),winner);
    }
}