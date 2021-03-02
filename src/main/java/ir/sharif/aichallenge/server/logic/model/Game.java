package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.common.network.data.*;
import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.handlers.AttackHandler;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.model.Colony.Colony;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.chatbox.ChatMessage;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;
import ir.sharif.aichallenge.server.logic.utility.MessageAdapter;
import ir.sharif.aichallenge.server.logic.dto.graphics.AttackDTO;
import ir.sharif.aichallenge.server.logic.dto.graphics.CellDTO;
import ir.sharif.aichallenge.server.logic.dto.graphics.GraphicLogDTO;
import ir.sharif.aichallenge.server.logic.dto.graphics.TurnDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Represents the Major class to control a game.
 */
public class Game {

    private AntRepository antRepository;
    private GameMap map;
    public int currentTurn = 0;
    private AttackHandler attackHandler;
    private MessageAdapter messageAdapter;
    private GameJudge gameJudge;
    public GraphicLogDTO graphicLogDTO = new GraphicLogDTO();

    // messages to be sent to clients in this turn
    private Message[] clientTurnMessages;
    private HashMap<Integer, Ant> newDeadAnts;

    /**
     * Create a Game with specific GameMap and Handlers.
     *
     * @param map           The gameMap of the Game.
     * @param colonyHashMap A HashMap from colonyId to Colony and contains game
     *                      colonies.
     */
    public Game(GameMap map, HashMap<Integer, Colony> colonyHashMap) {
        this.map = map;
        antRepository = new AntRepository(colonyHashMap);
        attackHandler = new AttackHandler(map, antRepository);
        messageAdapter = new MessageAdapter();
        gameJudge = new GameJudge(antRepository);
    }

    public void passTurn(Map<String, List<ClientMessageInfo>> messages) {
        messages = filterMessages(messages);
        attackHandler.handleAttacks();
        newDeadAnts = attackHandler.getNewDeadAnts();
        removeDeadAntsNewMessages(messages);
        handleChatMessages(messages);
        handleAntsMove(messages);
        map.getAllCells().forEach(Cell::manageResources);
        if (isFinished()) {
            Colony winnerColony = gameJudge.getWinner();
            Log.i("Game", "Game finished, winner colony id: " + winnerColony.getId());
            System.exit(0);
        }
        generateTurnGraphicLog();
        currentTurn++;
    }

    private void generateTurnGraphicLog() {
        TurnDTO turnLog = new TurnDTO();
        turnLog.turn_num = currentTurn;
        turnLog.base0_health = this.getColonies().get(0).getBaseHealth();
        turnLog.base1_health = this.getColonies().get(1).getBaseHealth();
        List<String> chat_box_0 = new ArrayList<>();
        this.getColonies().get(0).getChatBox().getChatMessages().forEach((msg) -> chat_box_0.add(msg.getMessage()));
        List<String> chat_box_1 = new ArrayList<>();
        this.getColonies().get(1).getChatBox().getChatMessages().forEach((msg) -> chat_box_1.add(msg.getMessage()));
        turnLog.chat_box_0 = chat_box_0;
        turnLog.chat_box_1 = chat_box_1;
        List<CellDTO> cells = new ArrayList<>();
        for (Cell cell : map.getAllCells()) {
            cells.add(new CellDTO(cell));
        }
        turnLog.cells = cells;
        List<AttackDTO> attacks = new ArrayList<>();


        // TODO: turnLog.attacks
    }

    public GameJudge getGameJudge() {
        return gameJudge;
    }

    // remove dead ants messages (if client is alive!)
    private Map<String, List<ClientMessageInfo>> filterMessages(Map<String, List<ClientMessageInfo>> messages) {
        Map<String, List<ClientMessageInfo>> filteredMessages = new HashMap<>();
        for (String key : messages.keySet()) {
            filteredMessages.put(key, new ArrayList<ClientMessageInfo>());
            for (ClientMessageInfo info : messages.get(key)) {
                if (antRepository.getAnt(info.getPlayerId()) != null) {
                    List<ClientMessageInfo> list = filteredMessages.get(key);
                    list.add(info);
                    filteredMessages.put(key, list);
                }
            }
        }
        return filteredMessages;
    }

    public HashMap<Integer, Ant> getNewDeadAnts() {
        return newDeadAnts;
    }

    private void handleChatMessages(Map<String, List<ClientMessageInfo>> messages) {
        Map<Integer, List<ClientMessageInfo>> groupedSendMessages = messages
                .getOrDefault(MessageTypes.SEND_MESSAGE, new ArrayList<>()).stream()
                .collect(Collectors.groupingBy(x -> antRepository.getAnt(x.getPlayerId()).getColonyId()));
        for (Integer colonyId : groupedSendMessages.keySet()) {
            addMessage(getColony(colonyId), groupedSendMessages.get(colonyId));
        }
        antRepository.getColony(1);
    }

    private void addMessage(Colony colony, List<ClientMessageInfo> messages) {
        List<ChatMessage> chatMessages = messageAdapter.convertToChatMessage(messages, currentTurn);
        colony.getChatBox().addMessage(chatMessages);
    }

    private void handleAntsMove(Map<String, List<ClientMessageInfo>> messages) {
        List<ActionInfo> actionMessages = messages.getOrDefault(MessageTypes.ACTION, new ArrayList<>()).stream()
                .map(x -> ((ActionInfo) (x))).collect(Collectors.toList());
        for (ActionInfo actionMessage : actionMessages) {
            Ant ant = antRepository.getAnt(actionMessage.getPlayerId());
            if (ant != null)
                map.changeAntCurrentCell(ant, actionMessage.getDirection());
        }
    }

    private void removeDeadAntsNewMessages(Map<String, List<ClientMessageInfo>> messages) {
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
        if (currentTurn >= ConstConfigs.GAME_MAXIMUM_TURN_COUNT) {
            return true;
        }
        for (Colony colony : antRepository.getColonies()) {
            if (colony.getBaseHealth() <= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isAntAlive(int antId) {
        System.out.println("ant id : " + antId + " exists: " + antRepository.doesAntExists(antId));
        return antRepository.doesAntExists(antId);
    }

    public void addAntToGame(Ant ant, Integer colonyId) throws GameActionException {
        antRepository.addAnt(ant, colonyId);
        map.getCell(ant.getXPosition(), ant.getYPosition()).addAnt(ant);
    }

    public GameMap getMap() {
        return map;
    }

    public Ant getAntByID(int antId) {
        return antRepository.getAnt(antId);
    }

    public Colony getColony(int colonyId) {
        return antRepository.getColony(colonyId);
    }

    public List<Colony> getColonies() {
        return antRepository.getColonies();
    }
}
