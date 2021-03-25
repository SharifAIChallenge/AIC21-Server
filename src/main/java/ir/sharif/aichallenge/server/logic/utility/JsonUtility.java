package ir.sharif.aichallenge.server.logic.utility;

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

public class JsonUtility {

    public static ExternalMap readMapFromFile(String fileName, int height, int width) throws IOException, ParseException {
        Cell[][] mapCells = new Cell[height][width];
        ExternalMap externalMap = new ExternalMap(mapCells);

        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fileName)) {
            JSONObject map = (JSONObject) jsonParser.parse(reader);
            JSONArray cells = (JSONArray) map.get("cells_type");

            for (Object o : cells) {
                Cell cell = parseCellObject((JSONObject) o);
                mapCells[cell.getY()][cell.getX()] = cell;
                if (cell.isBase()){
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
        //Get cell first name
        int yPosition = ((Long)cell.get("row")).intValue();
        System.out.println(yPosition);

        //Get cell last name
        int xPosition = ((Long)cell.get("col")).intValue();
        System.out.println(xPosition);

        //Get cell website name
        int cell_type_value = ((Long)cell.get("cell_type")).intValue();
        CellType cellType = CellType.getCellType(cell_type_value);
        System.out.println(cellType);

        ResourceType resourceType = ResourceType.NONE;
        int resourceAmount = 0;
//        int grass = ((Long)cell.get("res1")).intValue();
//        System.out.println(ResourceType.getResourceType(1));
//
//        int bread = ((Long)cell.get("res2")).intValue();
//        System.out.println(ResourceType.getResourceType(0));

//        if (grass != 0) {
//            resourceType = ResourceType.GRASS;
//            resourceAmount = grass;
//        } else if (bread != 0) {
//            resourceType = ResourceType.BREAD;
//            resourceAmount = bread;
//        } else {
//            resourceType = ResourceType.NONE;
//            resourceAmount = 0;
//        }

        if (cellType == CellType.BASE) {
            return new BaseCell(xPosition, yPosition);
        }
        return new Cell(xPosition, yPosition, cellType, resourceType, resourceAmount);
    }
}
