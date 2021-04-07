package ir.sharif.aichallenge.server.logic.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.logic.GameHandler;
import ir.sharif.aichallenge.server.logic.model.Game;
import ir.sharif.aichallenge.server.logic.utility.AntGenerator;

public class ConfigReader {
    public static void readConfigFile() {
        // app config
        Properties props = new Properties();
        String configFile = "map.config";
        InputStream is = null;
        try {
            is = new FileInputStream(configFile);
        } catch (FileNotFoundException ex) {
            Log.e("ConfigReader", "config file not found!");
            System.exit(4);
        }
        try {
            props.load(is);
        } catch (IOException ex) {
            Log.e("ConfigReader", "error loading config props");
            System.exit(4);
        }
        try {
            ConstConfigs.WORKER_ANT_INITIAL_HEALTH = Integer.parseInt(props.getProperty("WORKER_ANT_INITIAL_HEALTH"));
            ConstConfigs.SOLDIER_ANT_INITIAL_HEALTH = Integer.parseInt(props.getProperty("SOLDIER_ANT_INITIAL_HEALTH"));
            ConstConfigs.COLONY_INITIAL_BREAD = Integer.parseInt(props.getProperty("COLONY_INITIAL_BREAD"));
            ConstConfigs.COLONY_INITIAL_GRASS = Integer.parseInt(props.getProperty("COLONY_INITIAL_GRASS"));
            ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT = Integer
                    .parseInt(props.getProperty("GENERATE_WORKER_BREAD_AMOUNT"));
            ConstConfigs.GENERATE_SOLDIER_GRASS_AMOUNT = Integer
                    .parseInt(props.getProperty("GENERATE_SOLDIER_GRASS_AMOUNT"));
            ConstConfigs.ANT_MAX_VIEW_DISTANCE = Integer.parseInt(props.getProperty("ANT_MAX_VIEW_DISTANCE"));
            ConstConfigs.ANT_MAX_ATTACK_DISTANCE = Integer.parseInt(props.getProperty("ANT_MAX_ATTACK_DISTANCE"));
            ConstConfigs.ANT_ATTACK_DAMAGE = Integer.parseInt(props.getProperty("ANT_ATTACK_DAMAGE"));
            ConstConfigs.CHAT_LIMIT = Integer.parseInt(props.getProperty("CHAT_LIMIT"));
            ConstConfigs.MAX_MESSAGE_LENGTH = Integer.parseInt(props.getProperty("MAX_MESSAGE_LENGTH"));
            ConstConfigs.GAME_MAXIMUM_TURN_COUNT = Integer.parseInt(props.getProperty("GAME_MAXIMUM_TURN_COUNT"));
            ConstConfigs.RATE_DEATH_RESOURCE = Float.parseFloat(props.getProperty("RATE_DEATH_RESOURCE"));
            ConstConfigs.MAP_WIDTH = Integer.parseInt(props.getProperty("MAP_WIDTH"));
            ConstConfigs.MAP_HEIGHT = Integer.parseInt(props.getProperty("MAP_HEIGHT"));
            ConstConfigs.BASE_MAX_ATTACK_DISTANCE = Integer.parseInt(props.getProperty("BASE_MAX_ATTACK_DISTANCE"));
            ConstConfigs.BASE_ATTACK_DAMAGE = Integer.parseInt(props.getProperty("BASE_ATTACK_DAMAGE"));
            ConstConfigs.BASE_INIT_HEALTH = Integer.parseInt(props.getProperty("BASE_INIT_HEALTH"));
            ConstConfigs.READ_MAP_FROM_FILE = Boolean.parseBoolean(props.getProperty("READ_MAP_FROM_FILE"));
            GameHandler.initSoldiersNum = Integer.parseInt(props.getProperty("INIT_SCORPIONS"));
            GameHandler.initWorkersNum = Integer.parseInt(props.getProperty("INIT_ANTS"));
            try {
                AntGenerator.PROCESS_TIMEOUT_SECONDS = Integer.parseInt(props.getProperty("PROCESS_TIMEOUT_SECONDS"));
            } catch (Exception ignored) {
                AntGenerator.PROCESS_TIMEOUT_SECONDS = 30;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ConfigReader", "error in config props");
            System.exit(4);
        }
    }
}
