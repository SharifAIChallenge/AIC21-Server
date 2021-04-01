package ir.sharif.aichallenge.server.logic.handlers;

import ir.sharif.aichallenge.server.logic.model.GameJudge;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoreHandlerTest {

    //TODO ScoreHandler should be removed cause its not used.
    private List<String> players;
    private ScoreHandler scoreHandler;

    @BeforeEach
    void setUp() {
        players = new ArrayList<>();
        players.add("0");
        players.add("1");
        players.add("2");
        players.add("3");
        scoreHandler = new ScoreHandler(players);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void increaseOnePoint() {
    }

    @Test
    void decreaseOnePoint() {
    }

    @Test
    void getScores() {
    }
}