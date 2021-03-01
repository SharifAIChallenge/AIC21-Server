package ir.sharif.aichallenge.server.logic.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;

public class AntGenerator {
    static final String CLIENT_DIR = "client/";
    static final String WORKER_JAR = "worker.jar";
    static final String SOLDIER_JAR = "soldier.jar";
    static final String EXEC_CMD = "java -jar";

    public static void runNewAnt(AntType type, int antID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process p = Runtime.getRuntime()
                            .exec(EXEC_CMD + " " + CLIENT_DIR + ((type == AntType.SOLDIER) ? SOLDIER_JAR : WORKER_JAR));
                    Log.i("AntGenerator",
                            EXEC_CMD + " " + CLIENT_DIR + ((type == AntType.SOLDIER) ? SOLDIER_JAR : WORKER_JAR));
                    try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                        String line;

                        while ((line = input.readLine()) != null) {
                            Log.i("AntID: " + antID, line);
                            // System.out.println(antID + ":" + line);
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
