package ir.sharif.aichallenge.server.logic;

import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.common.network.data.ClientMessageInfo;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.common.network.data.MessageTypes;
import ir.sharif.aichallenge.server.engine.config.StringParam;
import ir.sharif.aichallenge.server.engine.core.GameLogic;
import ir.sharif.aichallenge.server.logic.dto.payloads.Token;
import ir.sharif.aichallenge.server.logic.model.Ant;
import ir.sharif.aichallenge.server.logic.model.Game;
import ir.sharif.aichallenge.server.logic.utility.MapGenerator;
import ir.sharif.aichallenge.server.logic.utility.RandomMapGenerator;

import java.util.*;

public class GameHandler implements GameLogic {

    private Game game;
    private static final StringParam[] CLIENT_NAMES = new StringParam[2];
    private static final int MAP_SIZE = 50;
    private MapGenerator mapGenerator;

    public GameHandler(MapGenerator mapGenerator) {
        CLIENT_NAMES[0] = new StringParam("Player 1", "player");
        CLIENT_NAMES[1] = new StringParam("Player 2", "player");
        this.mapGenerator = mapGenerator == null ? new RandomMapGenerator() : mapGenerator;
    }

    @Override
    public int getClientsNum() {
        return 2;
    }

    @Override
    public boolean[] getActiveClients() {
        return new boolean[]{true, true};
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
        List<String> players = new ArrayList<>();
        for (StringParam clientName : CLIENT_NAMES) {
            players.add(clientName.getValue());
        }
//        game = new Game();
    }

    @Override
    public Message getUIInitialMessage() {
        return null;
    }

    @Override
    public Message[] getClientInitialMessages() {
        Message[] initialMessages = new Message[2];

        initialMessages[0] = new Message(MessageTypes.INIT,
                Json.GSON.toJsonTree(new Token()).getAsJsonObject(), null);

        initialMessages[1] = new Message(MessageTypes.INIT,
                Json.GSON.toJsonTree(new Token()).getAsJsonObject(), null);

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
