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

import java.util.*;

import com.google.gson.JsonObject;

public class GameHandler implements GameLogic {

    private Game game;
    private Integer antsNum;
    private boolean showConsoleLog;

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
        MapGeneratorResult generatedMap = MapGenerator.generateRandomMap(10, 10);
        // create Game
        this.game = new Game(generatedMap.map, generatedMap.colonies);
        // add initial ants to game (for test)
        // antId, colonyId, x, y
        // one soldier for each
        antsNum = 2;
        Ant ant11 = new Ant(0, 0, 7, 0, AntType.WORKER);
        // Ant ant21 = new Ant(1, 0, 6, 0, AntType.SOLDIER);
        // Ant ant31 = new Ant(2, 0, 2, 0, AntType.WORKER);

        Ant ant211 = new Ant(1, 1, 7, 0, AntType.WORKER);
        // Ant ant22 = new Ant(3, 1, 6, 5, AntType.WORKER);
        // Ant ant23 = new Ant(5, 1, 7, 5, AntType.WORKER);

        try {
            game.addAntToGame(ant11, 0);
            // game.addAntToGame(ant21, 0);
            // game.addAntToGame(ant31, 0);
            game.addAntToGame(ant211, 1);
            // game.addAntToGame(ant22, 1);
            // game.addAntToGame(ant23, 1);
        } catch (GameActionException e) {
            System.out.println("Can't add ants to game!");
            e.printStackTrace();
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
    public void simulateEvents(Map<String, List<ClientMessageInfo>> messages) {
        if (game.getTurn() == 10) {
            System.exit(4);
        }
        game.passTurn(messages);
        showMap(true);
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
            } else {
                messages[i] = new Message(MessageTypes.GAME_STATUS,
                        Json.GSON.toJsonTree(new GameStatusDTO(this.game, i), GameStatusDTO.class).getAsJsonObject());
            }
        }
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
