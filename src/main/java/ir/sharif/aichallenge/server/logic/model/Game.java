package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.common.network.data.*;
import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.handlers.AttackHandler;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.ColonyNotExistsException;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.chatbox.ChatMessage;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;
import ir.sharif.aichallenge.server.logic.utility.MessageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Represents the Major class to control a game.
 */
public class Game {
    // maps colony ids to colony
    private HashMap<Integer, Colony> colonyHashMap;
    // maps ant ids to ant
    private HashMap<Integer, Ant> antHashMap;
    private GameMap map;
    public int currentTurn = 0;
    private AttackHandler attackHandler;
    private MessageAdapter messageAdapter;
    private GameJudge gameJudge;

    // messages to be sent to clients in this turn
    private Message[] clientTurnMessages;
    private HashMap<Integer, Ant> newDeadAnts;

    /**
     * Create a Game with specific GameMap and Handlers.
     *
     * @param map The gameMap of the Game. // * @param ownerHandler Handles all
     *            stuff about owner of nodes. // * @param turnHandler Handles turn
     *            logic of the game. // * @param scoreHandler The scoring system. //
     *            * @param validator The validator for players actions.
     */
    public Game(GameMap map, HashMap<Integer, Colony> colonyHashMap) {
        this.map = map;
        this.colonyHashMap = colonyHashMap;
        initAntHashMap();
        attackHandler = new AttackHandler(map, colonyHashMap, antHashMap);
        messageAdapter = new MessageAdapter();
        gameJudge = new GameJudge(this);
    }

    private void initAntHashMap() {
        antHashMap = new HashMap<>();
        for (Colony colony : colonyHashMap.values()) {
            for (Ant ant : colony.getAnts()) {
                antHashMap.put(ant.getId(), ant);
            }
        }
    }

    public void passTurn(Map<String, List<ClientMessageInfo>> messages) {
        // TODO: based on the messages in MessageTypes, do changes in game
        // for example:
        // increment turn
        // generate messages for clients and add them to [clientTurnMessages]
        // set [isGameFinished] when necessary
        // and many many other things :)
        attackHandler.handleAttacks();
        newDeadAnts = attackHandler.getNewDeadAnts();
        removeDeadAntsMessages(messages);
        handleChatMessages(messages);
        handleAntsMove(messages);
        map.getAllCells().forEach(Cell::manageResources);
        if(isFinished()){
            Colony winnerColony = gameJudge.getWinner();
        }
        currentTurn++;
    }

    private void handleChatMessages(Map<String, List<ClientMessageInfo>> messages) {
        Map<Integer, List<ClientMessageInfo>> groupedSendMessages = messages
                .getOrDefault(MessageTypes.SEND_MESSAGE, new ArrayList<>())
                .stream().collect(Collectors.groupingBy(x -> antHashMap.get(x.getPlayerId()).getColonyId()));
        for (Integer colonyId : groupedSendMessages.keySet()) {
            addMessage(getColony(colonyId), groupedSendMessages.get(colonyId));
        }
    }

    private void addMessage(Colony colony, List<ClientMessageInfo> messages) {
        List<ChatMessage> chatMessages = messageAdapter.convertToChatMessage(messages, currentTurn);
        colony.getChatBox().addMessage(chatMessages);
    }

    private void handleAntsMove(Map<String, List<ClientMessageInfo>> messages) {
        List<ActionInfo> actionMessages = messages
                .getOrDefault(MessageTypes.ACTION, new ArrayList<>())
                .stream().map(x -> ((ActionInfo) (x))).collect(Collectors.toList());
        for (ActionInfo actionMessage : actionMessages) {
            Ant ant = antHashMap.get(actionMessage.getPlayerId());
            map.moveAnt(ant, actionMessage.getDirection());
        }
    }

    private void removeDeadAntsMessages(Map<String, List<ClientMessageInfo>> messages) {
        for (String messageType : messages.keySet()) {
            messages.get(messageType).removeIf(x -> newDeadAnts.containsKey(x.getPlayerId()));
        }
    }

    public int getTurn() {
        return currentTurn;
    }

    public Message[] getClientTurnMessages() {
        return new Message[0];
    }

    public boolean isFinished() {
        if(currentTurn >= ConstConfigs.GAME_MAXIMUM_TURN_COUNT){
            return true;
        }
        for (Colony colony : colonyHashMap.values()) {
            if(colony.getBaseHealth() <= 0){
                return true;
            }
        }
        return false;
    }

    public boolean isAntAlive(int antId) {
        return antHashMap.containsKey(antId);
    }

    public void addAntToGame(Ant ant, Integer colonyId) throws GameActionException {
        Colony colony = colonyHashMap.get(colonyId);
        if (colony == null) {
            throw new ColonyNotExistsException("", colonyId);
        }
        colony.addNewAnt(ant);
        antHashMap.put(ant.getId(), ant);
    }


    public Ant getAntByID(int antId) {
        return antHashMap.getOrDefault(antId, null);
    }

    public GameMap getMap() {
        return map;
    }

    public Colony getColony(int colonyId) {
        return colonyHashMap.get(colonyId);
    }

    public List<Colony> getColonies() {
        return (List<Colony>) colonyHashMap.values();
    }
}
