package ir.sharif.aichallenge.server.logic;

import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.common.network.data.ClientMessageInfo;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.common.network.data.MessageTypes;
import ir.sharif.aichallenge.server.engine.config.StringParam;
import ir.sharif.aichallenge.server.engine.core.GameLogic;
import ir.sharif.aichallenge.server.logic.dto.payloads.Token;
import ir.sharif.aichallenge.server.logic.model.Game;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;

import java.util.*;
import java.util.stream.Collectors;

public class GameHandler implements GameLogic {

    private Game game;
    private ArrayList<Integer> antIds;

    public GameHandler() {
        this.antIds = new ArrayList<>();
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
        return 0;
    }

    @Override
    public void init() {
        // TODO: more than two ants
        // GameMap map = new GameMap(cells, width, height)
        // game = new Game();
    }

    @Override
    public Message getUIInitialMessage() {
        return null;
    }

    @Override
    public Message[] getClientInitialMessages() {
        Message[] initialMessages = new Message[2];

        initialMessages[0] = new Message(MessageTypes.INIT, Json.GSON.toJsonTree(new Token()).getAsJsonObject(), null);

        initialMessages[1] = new Message(MessageTypes.INIT, Json.GSON.toJsonTree(new Token()).getAsJsonObject(), null);

        return initialMessages;
    }

    @Override
    public void simulateEvents(Map<String, List<ClientMessageInfo>> messages) {
        game.passTurn(messages);
        // TODO: mohsen, error handling maybe!
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
        return game.getClientTurnMessages();
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
