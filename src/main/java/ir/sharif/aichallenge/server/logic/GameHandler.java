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
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.map.MapGenerator;
import ir.sharif.aichallenge.server.logic.model.map.MapGenerator.MapGeneratorResult;

import java.util.*;

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
        Ant ant1 = new Ant(0, 0, 0, 0, AntType.SOLDIER);
        Ant ant2 = new Ant(1, 1, 5, 5, AntType.SOLDIER);
        try {
            game.addAntToGame(ant1, 0);
            game.addAntToGame(ant2, 1);
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
        // game.passTurn(messages);
        showMap();
    }

    private void showMap() {
        System.out.println("--------this turn--------");
        for (Cell cell : game.getMap().getAllCells()) {
            System.out.println("[ " + cell.getX() + ", " + cell.getY() + "]: " + cell.getCellType().toString() + " --> "
                    + getAntsIds(cell.getAnts()));
        }
        System.out.println();
    }

    private String getAntsIds(List<Ant> ants) {
        String result = "";
        for (Ant a : ants) {
            result += " [" + a.getId() + ":" + (a.getAntType().toString()) + " ]";
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

        // Send game status to each ant
        Message[] messages = new Message[antsNum];
        for (int i = 0; i < antsNum; i++) {
            messages[i] = new Message(MessageTypes.GAME_STATUS,
                    Json.GSON.toJsonTree(new GameStatusDTO(this.game, i), GameStatusDTO.class).getAsJsonObject());
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
