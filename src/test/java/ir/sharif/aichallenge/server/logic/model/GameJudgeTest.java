package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.model.Colony.Colony;
import ir.sharif.aichallenge.server.logic.model.Colony.ColonyBuilder;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.cell.BaseCell;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.map.MapGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

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
    void getWinner0() {
        antRepository.getColony(0).decreaseBaseHealth(1);
        Colony winner = gameJudge.getWinner();
        assertEquals(antRepository.getColony(1),winner);
    }

    @Test
    void getWinner1() {
        ColonyBuilder colonyBuilder1 = new ColonyBuilder(0,0);
        colonyBuilder1.setBaseCell(new BaseCell(0,0), 5);
        Colony colony1=  colonyBuilder1.getColony();

        ColonyBuilder colonyBuilder2 = new ColonyBuilder(1, 1);
        colonyBuilder2.setBaseCell(new BaseCell(100,100),5);
        Colony colony2 = colonyBuilder2.getColony();
        Ant ant = new Ant(0,1,100,100, AntType.SOLDIER);
        try {
            colony2.addNewAnt(ant);
        } catch (GameActionException e) {
            e.printStackTrace();
            fail();
        }
        HashMap<Integer, Colony> colonyHashMap = new HashMap<>();
        colonyHashMap.put(colony1.getId(), colony1);
        colonyHashMap.put(colony2.getId(), colony2);
        antRepository = new AntRepository(colonyHashMap);
        gameJudge = new GameJudge(antRepository);

        Colony winner = gameJudge.getWinner();
        assertEquals(antRepository.getColony(1),winner);
    }

    @Test
    void getWinner2() {
        ColonyBuilder colonyBuilder1 = new ColonyBuilder(0,0);
        colonyBuilder1.setBaseCell(new BaseCell(0,0), 6);
        Colony colony1=  colonyBuilder1.getColony();

        ColonyBuilder colonyBuilder2 = new ColonyBuilder(1, 1);
        colonyBuilder2.setBaseCell(new BaseCell(100,100),5);
        Colony colony2 = colonyBuilder2.getColony();
        Ant ant = new Ant(0,1,100,100, AntType.WORKER);
        try {
            colony2.addNewAnt(ant);
        } catch (GameActionException e) {
            e.printStackTrace();
            fail();
        }
        HashMap<Integer, Colony> colonyHashMap = new HashMap<>();
        colonyHashMap.put(colony1.getId(), colony1);
        colonyHashMap.put(colony2.getId(), colony2);
        antRepository = new AntRepository(colonyHashMap);
        gameJudge = new GameJudge(antRepository);

        Colony winner = gameJudge.getWinner();
        assertEquals(antRepository.getColony(0),winner);
    }
}