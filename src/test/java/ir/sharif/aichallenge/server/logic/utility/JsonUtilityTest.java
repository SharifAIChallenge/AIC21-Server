package ir.sharif.aichallenge.server.logic.utility;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

class JsonUtilityTest {

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void read() {

        try {
            JsonUtility.readMapFromFile("map.json");
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            fail();
        }
    }
}