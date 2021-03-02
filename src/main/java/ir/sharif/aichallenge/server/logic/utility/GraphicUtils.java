package ir.sharif.aichallenge.server.logic.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.logic.dto.graphics.GraphicLogDTO;

public class GraphicUtils {
    static final String outputFileName = "test1.json";

    public static void generateLogFile(GraphicLogDTO log) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
            writer.write(Json.GSON.toJson(log, GraphicLogDTO.class));
            writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
