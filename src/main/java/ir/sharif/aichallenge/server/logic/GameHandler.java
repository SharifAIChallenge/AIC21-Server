package ir.sharif.aichallenge.server.logic;

import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.common.network.data.ActionInfo;
import ir.sharif.aichallenge.server.common.network.data.ClientMessageInfo;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.common.network.data.MessageTypes;
import ir.sharif.aichallenge.server.engine.core.GameLogic;
import ir.sharif.aichallenge.server.logic.dto.payloads.GameConfigDTO;
import ir.sharif.aichallenge.server.logic.dto.payloads.GameStatusDTO;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.model.Game;
import ir.sharif.aichallenge.server.logic.model.Colony.Colony;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.cell.CellType;
import ir.sharif.aichallenge.server.logic.model.chatbox.ChatMessage;
import ir.sharif.aichallenge.server.logic.model.map.MapGenerator;
import ir.sharif.aichallenge.server.logic.model.map.MapGenerator.MapGeneratorResult;
import ir.sharif.aichallenge.server.logic.utility.AntGenerator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;

import javax.naming.spi.DirStateFactory.Result;

import com.google.gson.JsonObject;

public class GameHandler implements GameLogic {

    private Game game;
    private Integer antsNum;
    private boolean showConsoleLog;
    private ArrayList<Integer> deads = new ArrayList<>();
    private boolean newAntsCreated = false;
    private List<Integer> newAntIDs = new ArrayList<>();

    public GameHandler(boolean showConsoleLog) {
        this.antsNum = 0;
        this.showConsoleLog = showConsoleLog;
    }

    @Override
    public int getClientsNum() {
        return antsNum;
    }

    @Override
    public boolean[] getActiveClients() {
        boolean[] bitArray = new boolean[antsNum];
        for (int i = 0; i < antsNum; i++) {
            bitArray[i] = game.isAntAlive(i);
        }
        return bitArray;
    }

    @Override
    public long getClientResponseTimeout() {
        // 3 seconds!
        return 3000;
    }

    @Override
    public long getTurnTimeout() {
        return 3000;
    }

    @Override
    public void init() {
        // TODO: map generation
        // generate map
        MapGeneratorResult generatedMap = MapGenerator.generateRandomMap();
        // create Game
        this.game = new Game(generatedMap.map, generatedMap.colonies);
        // add initial ants to game (for test)
        // antId, colonyId, x, y
        // one soldier for each
        antsNum = 8;
        ArrayList<Ant> initialAnts = new ArrayList<>();
        for (int i = 0; i < antsNum; i++) {
            int colonyID = (i < 4) ? 0 : 1;
            initialAnts.add(new Ant(i, colonyID, generatedMap.colonies.get(colonyID).getBase().getX(),
                    generatedMap.colonies.get(colonyID).getBase().getY(),
                    ((i % 2) == 1) ? AntType.WORKER : AntType.SOLDIER));
        }
        try {
            for (Ant ant : initialAnts) {
                game.addAntToGame(ant, ant.getColonyId());
            }
        } catch (GameActionException e) {
            System.out.println("Can't add ants to game!");
            e.printStackTrace();
        }

        for (Ant ant : initialAnts) {
            AntGenerator.runNewAnt(ant.getAntType(), ant.getId());
        }

    }

    @Override
    public Message getUIInitialMessage() {
        return null;
    }

    @Override
    public Message[] getClientInitialMessages() {
        Message[] initialMessages = new Message[antsNum];

        // send game config to Ants!
        for (int i = 0; i < antsNum; i++) {
            initialMessages[i] = new Message(MessageTypes.INIT,
                    Json.GSON.toJsonTree(new GameConfigDTO(this.game, i), GameConfigDTO.class).getAsJsonObject());
        }

        System.out.println("initial messages created");
        return initialMessages;
    }

    @Override
    public ArrayList<Integer> simulateEvents(Map<String, List<ClientMessageInfo>> messages) {
        ArrayList<Integer> result = new ArrayList<>();
        if (game.getTurn() == 5) {
            System.exit(4);
        }
        game.passTurn(messages);
        result = handleAntGeneration();
        showMap(true);
        return result;
    }

    private ArrayList<Integer> handleAntGeneration() {
        ArrayList<Integer> result = new ArrayList<>();
        for (Colony colony : game.getColonies()) {
            int soldiers = colony.getToBeGeneratedSoldiersCount();
            for (int i = 0; i < soldiers; i++) {
                result.add(
                        addNewAnt(colony.getBase().getX(), colony.getBase().getY(), colony.getId(), AntType.SOLDIER));
            }
            int workers = colony.getToBeGeneratedWorkersCount();
            for (int i = 0; i < workers; i++) {
                result.add(addNewAnt(colony.getBase().getX(), colony.getBase().getY(), colony.getId(), AntType.WORKER));
            }
            colony.setToBeGeneratedSoldiersCount(0);
            colony.setToBeGeneratedWorkersCount(0);
        }

        return result;
    }

    private int addNewAnt(int x, int y, int colonyID, AntType type) {
        antsNum++;
        int id = antsNum - 1;
        newAntsCreated = true;
        newAntIDs.add(id);
        Ant ant = new Ant(id, colonyID, x, y, type);
        try {
            game.addAntToGame(ant, colonyID);
        } catch (GameActionException e) {
            e.printStackTrace();
        }
        AntGenerator.runNewAnt(type, id);
        return id;
    }

    private void showMap(boolean showChatbox) {
        System.out.println("--------this turn: " + (game.getTurn() - 1) + "--------");
        for (Cell cell : game.getMap().getAllCells()) {
            System.out.println("[" + cell.getX() + "," + cell.getY() + "]: " + cell.getCellType().toString() + " "
                    + cell.getResourceType().toString() + ":" + cell.getResourceAmount() + " --> "
                    + getAntsIds(cell.getAnts()));
        }
        System.out.println();
        if (showChatbox) {
            for (Colony colony : game.getColonies()) {
                System.out.println("chatbox for colony: " + colony.getId() + " with health: " + colony.getBaseHealth()
                        + " bread:" + colony.getGainedBread() + " grass:" + colony.getGainedGrass());
                for (ChatMessage message : colony.getChatBox().getChatMessages()) {
                    System.out.println(Json.GSON.toJson(message, ChatMessage.class));
                }
            }
        }
        System.out.println();
    }

    private String getAntsIds(List<Ant> ants) {
        String result = "";
        for (Ant a : ants) {
            result += " [" + Json.GSON.toJson(a, Ant.class) + "] ";
        }
        return result;
    }

    @Override
    public void generateOutputs() {
        // AIC 2019 - Graphic
    }

    @Override
    public Message getUIMessage() {
        return null;
    }

    @Override
    public Message getStatusMessage() {
        return null;
    }

    @Override
    public Message[] getClientMessages() {
        if (game.getTurn() == 0) {
            return getClientInitialMessages();
        }

        // dead ants
        HashMap<Integer, Ant> deadAnts = game.getNewDeadAnts();

        // Send game status to each ant
        Message[] messages = new Message[antsNum];
        for (int i = 0; i < antsNum; i++) {
            if (deadAnts != null && deadAnts.keySet().contains(i)) {
                messages[i] = new Message(MessageTypes.KILL, new JsonObject());
                deads.add(i);
            } else {
                if (!deads.contains(i)) {
                    if (newAntsCreated && newAntIDs.contains(i)) {
                        messages[i] = new Message(MessageTypes.INIT, Json.GSON
                                .toJsonTree(new GameConfigDTO(this.game, i), GameConfigDTO.class).getAsJsonObject());
                    } else {
                        messages[i] = new Message(MessageTypes.GAME_STATUS, Json.GSON
                                .toJsonTree(new GameStatusDTO(this.game, i), GameStatusDTO.class).getAsJsonObject());
                        if (i == 0) {
                            System.out.println("message to ant " + i + ": "
                                    + Json.GSON.toJson(new GameStatusDTO(this.game, i), GameStatusDTO.class));
                        }
                    }
                }
            }
        }
        newAntsCreated = false;
        return messages;
    }

    @Override
    public Message[] getClientEndMessages() {
        // no need to send message at end now!
        return new Message[0];
    }

    @Override
    public boolean isGameFinished() {
        return game.isFinished();
    }

    @Override
    public void terminate() {
        // terminate some graphic things for example!
        // produce some logs if necessary
    }
}
