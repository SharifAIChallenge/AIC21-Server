package ir.sharif.aichallenge.server.logic;

import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.common.network.data.ClientMessageInfo;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.common.network.data.MessageTypes;
import ir.sharif.aichallenge.server.engine.config.StringParam;
import ir.sharif.aichallenge.server.engine.core.GameLogic;
import ir.sharif.aichallenge.server.logic.dto.payloads.GameConfigDTO;
import ir.sharif.aichallenge.server.logic.dto.payloads.GameStatusDTO;
import ir.sharif.aichallenge.server.logic.dto.payloads.Token;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.model.Colony;
import ir.sharif.aichallenge.server.logic.model.Game;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;
import ir.sharif.aichallenge.server.logic.model.map.MapGenerator;
import ir.sharif.aichallenge.server.logic.model.map.MapGenerator.MapGeneratorResult;

import java.util.*;
import java.util.stream.Collectors;

public class GameHandler implements GameLogic {

    private Game game;
    private ArrayList<Integer> antIds;
    private boolean showConsoleLog;

    public GameHandler(boolean showConsoleLog) {
        this.antIds = new ArrayList<>();
        this.showConsoleLog = showConsoleLog;
    }

    @Override
    public int getClientsNum() {
        return antIds.size();
    }

    @Override
    public boolean[] getActiveClients() {
        boolean[] bitArray = new boolean[antIds.size()];
        for (int i = 0; i < antIds.size(); i++) {
            bitArray[i] = game.isAntAlive(antIds.get(i));
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
        MapGeneratorResult generatedMap = MapGenerator.generateRandomMap(20, 10);
        // create Game
        this.game = new Game(generatedMap.map, generatedMap.colonies);
        // add initial ants to game (for test)
        Ant ant1 = new Ant(10, 0, 0, 0, AntType.SOLDIER);
        Ant ant2 = new Ant(11, 1, 10, 10, AntType.SOLDIER);
        antIds.add(10);
        antIds.add(11);
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
        Message[] initialMessages = new Message[2];

        // send game config to Ants!
        initialMessages[0] = new Message(MessageTypes.INIT, Json.GSON.toJsonTree
                (new GameConfigDTO(this.game, antIds.get(0)), GameConfigDTO.class).getAsJsonObject());

        initialMessages[1] = new Message(MessageTypes.INIT, Json.GSON.toJsonTree
                (new GameConfigDTO(this.game, antIds.get(1)), GameConfigDTO.class).getAsJsonObject());


        System.out.println("initial messages returned....");
        return initialMessages;
    }

    @Override
    public void simulateEvents(Map<String, List<ClientMessageInfo>> messages) {
        // TODO: pass one turn
        // game.passTurn(messages);
        game.currentTurn++;
    }

    @Override
    public void generateOutputs() {

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
        Message[] messages = new Message[antIds.size()];
        for (int i = 0; i < antIds.size(); i++) {
            messages[i] = new Message(MessageTypes.GAME_STATUS, Json.GSON.toJsonTree(
                    new GameStatusDTO(this.game, antIds.get(i)), GameStatusDTO.class
            ).getAsJsonObject());
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
