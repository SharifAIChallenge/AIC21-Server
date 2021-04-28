package ir.sharif.aichallenge.server.logic.utility;

import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.model.cell.BaseCell;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.cell.CellType;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;
import ir.sharif.aichallenge.server.logic.model.map.ExternalMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class JsonUtility {

    public static ExternalMap readMapFromFile(String fileName) throws IOException, ParseException {

        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fileName)) {
            JSONObject map = (JSONObject) jsonParser.parse(reader);
            try {
                ConstConfigs.MAP_HEIGHT = ((Long) map.get("MAP_HEIGHT")).intValue();
                ConstConfigs.MAP_WIDTH = ((Long) map.get("MAP_WIDTH")).intValue();
                try {
                    ConstConfigs.SHIFT_X = ((Long) map.get("SHIFT_X")).intValue();
                } catch (Exception ignore) {
                }
                try {
                    ConstConfigs.SHIFT_Y = ((Long) map.get("SHIFT_Y")).intValue();
                } catch (Exception ignore) {
                }
            } catch (Exception e) {
                Log.e("JsonUtility", "MAP_HEIGHT or MAP_WIDTH is not available in map.json!");
                System.exit(-1);
            }
            JSONArray cells = (JSONArray) map.get("cells_type");
            int height = ConstConfigs.MAP_HEIGHT;
            int width = ConstConfigs.MAP_WIDTH;
            Cell[][] mapCells = new Cell[height][width];
            ExternalMap externalMap = new ExternalMap(mapCells);
            for (Object o : cells) {
                Cell cell = parseCellObject((JSONObject) o);
                // (cell.getY() + " " + cell.getX());
                mapCells[cell.getY()][cell.getX()] = cell;
                if (cell.isBase()) {
                    externalMap.addBaseCell((BaseCell) cell);
                }
            }

            return externalMap;
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            throw e;
        }
    }

    private static Cell parseCellObject(JSONObject cell) {
        // Get cell first name
        int yPosition = ((Long) cell.get("row")).intValue();
        // (yPosition);

        // Get cell last name
        int xPosition = ((Long) cell.get("col")).intValue();
        // (xPosition);

        // Get cell website name
        int cell_type_value = ((Long) cell.get("cell_type")).intValue();
        cell_type_value = cell_type_value >= 1 ? cell_type_value - 1 : cell_type_value;
        CellType cellType = CellType.getCellType(cell_type_value);
        // (cellType);

        ResourceType resourceType = ResourceType.NONE;
        int resourceAmount = 0;
        int grass = ((Long) cell.get("rec1")).intValue();
        // (ResourceType.getResourceType(1));

        int bread = ((Long) cell.get("rec2")).intValue();
        // (ResourceType.getResourceType(0));

        if (grass != 0) {
            resourceType = ResourceType.GRASS;
            resourceAmount = grass;
        } else if (bread != 0) {
            resourceType = ResourceType.BREAD;
            resourceAmount = bread;
        } else {
            resourceType = ResourceType.NONE;
            resourceAmount = 0;
        }

        if (cellType == CellType.BASE) {
            return new BaseCell(xPosition, yPosition);
        }
        // future resource
        if (cell_type_value >= 6) {
            int toBeAddedTurn = ThreadLocalRandom.current().nextInt(1, ConstConfigs.GAME_MAXIMUM_TURN_COUNT);
            return new Cell(xPosition, yPosition, resourceType, resourceAmount, toBeAddedTurn);
        }
        return new Cell(xPosition, yPosition, cellType, resourceType, resourceAmount);
    }
}
