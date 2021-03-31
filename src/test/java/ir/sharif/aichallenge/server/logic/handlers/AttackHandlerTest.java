package ir.sharif.aichallenge.server.logic.handlers;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.model.AntRepository;
import ir.sharif.aichallenge.server.logic.model.Colony.Colony;
import ir.sharif.aichallenge.server.logic.model.Colony.ColonyBuilder;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;
import ir.sharif.aichallenge.server.logic.model.map.MapBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AttackHandlerTest {

    private AttackHandler attackHandler;
    private AntRepository antRepository;
    private GameMap gameMap;
    private HashMap<Integer, Colony> colonyHashMap;

    @BeforeEach
    void setUp() {
        colonyHashMap = new HashMap<>();

        MapBuilder mapBuilder = new MapBuilder();
        mapBuilder.generateRandomMap();

        ColonyBuilder colonyBuilder;
        int colonyId;

        colonyId = 0;
        colonyBuilder = new ColonyBuilder(colonyId, -1);
        mapBuilder.addColony(colonyBuilder, ConstConfigs.BASE_INIT_HEALTH, 0, 0);
        colonyHashMap.put(colonyId, colonyBuilder.getColony());

        colonyId = 1;
        colonyBuilder = new ColonyBuilder(colonyId, -3);
        mapBuilder.addColony(colonyBuilder, ConstConfigs.BASE_INIT_HEALTH);
        colonyHashMap.put(colonyId, colonyBuilder.getColony());

        colonyId = 2;
        colonyBuilder = new ColonyBuilder(colonyId, -2);
        mapBuilder.addColony(colonyBuilder, 1, 1, 1);
        colonyHashMap.put(colonyId, colonyBuilder.getColony());


        antRepository = new AntRepository(colonyHashMap);
        gameMap = mapBuilder.build();
        attackHandler = new AttackHandler(this.gameMap, this.antRepository);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void handleBaseAttacks() {
        attackHandler.handleAttacks();
        assertEquals(1 - ConstConfigs.BASE_ATTACK_DAMAGE, colonyHashMap.get(2).getBaseHealth());
        assertEquals(ConstConfigs.BASE_INIT_HEALTH - ConstConfigs.BASE_ATTACK_DAMAGE, colonyHashMap.get(0).getBaseHealth());
        assertEquals(ConstConfigs.BASE_INIT_HEALTH, colonyHashMap.get(1).getBaseHealth());
    }

    @Test
    void handleAttacks() {
        Ant ant1 = new Ant(0, 0, 5, 5, AntType.SOLDIER);
        Ant ant2 = new Ant(1, 1, 5, 5, AntType.SOLDIER);
        try {
            colonyHashMap.get(0).addNewAnt(ant1);
            colonyHashMap.get(1).addNewAnt(ant2);
            gameMap.getCell(5, 5).addAnt(ant1);
            gameMap.getCell(5, 5).addAnt(ant2);
            antRepository.addAnt(ant1, 0);
            antRepository.addAnt(ant2, 1);
        } catch (GameActionException e) {
            e.printStackTrace();
        }

        attackHandler.handleAttacks();
        assertEquals(ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH - ConstConfigs.ANT_ATTACK_DAMAGE, ant1.getHealth());
        assertEquals(ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH - ConstConfigs.ANT_ATTACK_DAMAGE, ant2.getHealth());

        attackHandler.handleAttacks();
        assertEquals(ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH - 2 * ConstConfigs.ANT_ATTACK_DAMAGE, ant1.getHealth());
        assertEquals(ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH - 2 * ConstConfigs.ANT_ATTACK_DAMAGE, ant2.getHealth());
    }

    @Test
    void getNewDeadAnts() {
        Ant ant1 = new Ant(0, 0, 5, 5, AntType.SOLDIER);
        Ant ant2 = new Ant(1, 1, 5, 5, AntType.SOLDIER);
        try {
            colonyHashMap.get(0).addNewAnt(ant1);
            colonyHashMap.get(1).addNewAnt(ant2);
            gameMap.getCell(5, 5).addAnt(ant1);
            gameMap.getCell(5, 5).addAnt(ant2);
            antRepository.addAnt(ant1, 0);
            antRepository.addAnt(ant2, 1);
        } catch (GameActionException e) {
            e.printStackTrace();
        }

        attackHandler.handleAttacks();
        assertEquals(ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH - ConstConfigs.ANT_ATTACK_DAMAGE, ant1.getHealth());
        assertEquals(ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH - ConstConfigs.ANT_ATTACK_DAMAGE, ant2.getHealth());
        assertTrue(attackHandler.getNewDeadAnts().isEmpty());

        attackHandler.handleAttacks();
        assertEquals(ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH - 2 * ConstConfigs.ANT_ATTACK_DAMAGE, ant1.getHealth());
        assertEquals(ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH - 2 * ConstConfigs.ANT_ATTACK_DAMAGE, ant2.getHealth());
        assertEquals(2, attackHandler.getNewDeadAnts().size());
    }

    @Test
    void getNearByAttacks() {
        Ant ant1 = new Ant(0, 0, 1, 1, AntType.SOLDIER);
        Ant ant2 = new Ant(1, 1, 1, 1, AntType.SOLDIER);
        try {
            colonyHashMap.get(0).addNewAnt(ant1);
            colonyHashMap.get(1).addNewAnt(ant2);
            gameMap.getCell(1, 1).addAnt(ant1);
            gameMap.getCell(1, 1).addAnt(ant2);
            antRepository.addAnt(ant1, 0);
            antRepository.addAnt(ant2, 1);
        } catch (GameActionException e) {
            e.printStackTrace();
        }

        attackHandler.handleAttacks();
        List<AttackSummary> attackSummaries = attackHandler.getAttackSummaries();
        List<AttackSummary> attackSummaryList = attackHandler.getNearByAttacks(ant1.getId());
        assertEquals(4, attackSummaryList.size());
    }
}