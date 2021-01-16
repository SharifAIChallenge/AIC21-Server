package ir.sharif.aichallenge.server.engine.core;

import ir.sharif.aichallenge.server.common.network.data.ClientMessageInfo;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.engine.config.IntegerParam;

import java.util.List;
import java.util.Map;

/**
 * The abstract class representing the main game logic of the user's game.
 * <p>
 * This class will be the simulator engine of the game.
 * </p>
 */
public interface GameLogic {

    IntegerParam currentTurn = new IntegerParam("CurrentTurn", -1);

    /**
     * Returns number of players.
     *
     * @return number of players
     */
    public int getClientsNum();

    boolean[] getActiveClients();

    public long getClientResponseTimeout();

    public long getTurnTimeout();

    /**
     * This method must send initial and necessary values to UI and clients.
     */
    public void init();

    /**
     * @return UI initial message
     */
    public Message getUIInitialMessage();

    /**
     * @return Client initial message
     */
    public Message[] getClientInitialMessages();

    /**
     * Simulate events based on the current turn event and calculate the changes in game.
     *
     * @param events Events that is happened in the game. It is a map from type of event to event itself
     */
    public void simulateEvents(Map<String, List<ClientMessageInfo>> events);

    /**
     * This method generates the output based on the changes that were calculated in
     * {@link #simulateEvents}.
     */
    public void generateOutputs();

    public Message getUIMessage();

    public Message getStatusMessage();

    public Message[] getClientMessages();

    public Message[] getClientEndMessages();   //Added at AIC20

    public boolean isGameFinished();

    public void terminate();
}
