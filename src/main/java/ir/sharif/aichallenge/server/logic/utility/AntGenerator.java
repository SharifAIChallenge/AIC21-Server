package ir.sharif.aichallenge.server.logic.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedDeque;

import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;

public class AntGenerator {
    static final String CLIENT_DIR = "client/";
    static final String WORKER_JAR = "worker.jar";
    static final String SOLDIER_JAR = "soldier.jar";
    static final String EXEC_CMD = "java -jar";

    static ConcurrentLinkedDeque<Process> processes = new ConcurrentLinkedDeque();

    public static void runNewAnt(AntType type, int antID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process p = Runtime.getRuntime()
                            .exec(EXEC_CMD + " " + CLIENT_DIR + ((type == AntType.SOLDIER) ? SOLDIER_JAR : WORKER_JAR));
                    AntGenerator.processes.add(p);
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

    // works in linux
    public static void killAnts() {
        for (Process p : AntGenerator.processes) {
            p.destroy();
        }
        try {
            Process p = Runtime.getRuntime().exec("pkill -f \"java -jar\"");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
