package ir.sharif.aichallenge.server.logic;

import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.engine.core.GameServer;
import ir.sharif.aichallenge.server.logic.utility.AntGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        Runtime.getRuntime().addShutdownHook(new ShutDownHookThread());
        AtomicInteger currentTurn = new AtomicInteger(0);
        new File("Log/server").mkdirs();
        Log.outputFile = new PrintStream(new FileOutputStream("Log/server/server.log", false));
        // int extraTime = extractExtraTime(args);

        GameServer gameServer = new GameServer(new GameHandler(), args, currentTurn);
        gameServer.start();
        gameServer.waitForFinish();
    }

    private static int extractExtraTime(String[] args) {
        int extraTime = 0;
        try {
            for (String arg : args) {
                if (!arg.startsWith("--extra=") && !arg.startsWith("--extra:")) {
                    continue;
                }
                extraTime = Integer.parseInt(arg.substring(8));
                return extraTime;
            }
        } catch (Exception e) {
            return extraTime;
        }

        return extraTime;
    }
}

class ShutDownHookThread extends Thread {
    @Override
    public void run() {
        Log.i("Server", "\u001B[31m" + "Bye!" + "\u001B[0m");
        // kill ants
        AntGenerator.killAnts();
        super.run();
    }
}
