package ir.sharif.aichallenge.server.logic.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentLinkedDeque;

import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.engine.config.Configs;
import ir.sharif.aichallenge.server.logic.GameHandler;
import ir.sharif.aichallenge.server.logic.model.Game;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;

public class AntGenerator {
    static final String CLIENT_BASE_DIR = "client/";
    static final String TEAM_1_DIR = "1/";
    static final String TEAM_2_DIR = "2/";
    static final String WORKER_JAR = "worker.jar";
    static final String SOLDIER_JAR = "soldier.jar";
    static final String JAVA_EXEC_CMD = "java -jar";

    static ConcurrentLinkedDeque<Process> processes = new ConcurrentLinkedDeque();

    public static void runNewAnt(AntType type, int antID, int colonyID) {
        if (GameHandler.runManually) {
            Log.i("AntGenerator", "\u001B[32m" + " Run a new instance of your client, waiting... " + "\u001B[0m");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process p = Runtime.getRuntime().exec(getRunCMD(colonyID));
                    AntGenerator.processes.add(p);
                    Log.i("AntGenerator", getRunCMD(colonyID));
                    try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                        if (GameHandler.showGameLog) {
                            String line = input.readLine();
                            while (line != null) {
                                Log.i("Client Output[" + antID + "]", line);
                                // System.out.println(antID + ":" + line);
                                line = input.readLine();
                            }
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static String getRunCMD(int colonyID) {
        String path = colonyID == 0 ? Configs.FIRST_TEAM_PATH : Configs.SECOND_TEAM_PATH;
        if (path.contains(".jar")) {
            return JAVA_EXEC_CMD + " " + path;
        } else {
            if (path.charAt(0) == '/') {
                return path;
            } else {
                return "./" + path;
            }
        }
    }

    // works in linux
    public static void killAnts() {
        for (Process p : AntGenerator.processes) {
            p.destroy();
        }
        try {
            Process p = Runtime.getRuntime().exec("pkill -f \"java -jar\"");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
        }
    }
}
