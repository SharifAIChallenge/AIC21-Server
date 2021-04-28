package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.common.network.data.*;
import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.logic.GameHandler;
import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.handlers.AttackHandler;
import ir.sharif.aichallenge.server.logic.handlers.AttackSummary;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.model.Colony.Colony;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.chatbox.ChatMessage;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;
import ir.sharif.aichallenge.server.logic.utility.MessageAdapter;
import ir.sharif.aichallenge.server.logic.dto.graphics.AttackDTO;
import ir.sharif.aichallenge.server.logic.dto.graphics.CellDTO;
import ir.sharif.aichallenge.server.logic.dto.graphics.ChatElementDTO;
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
    public static QuickResult quickResult = new QuickResult();

    /**
     * Create a Game with specific GameMap and Handlers.
     *
     * @param map           The gameMap of the Game.
     * @param antRepository Game ants and colonies can be reached from this object.
     */
    public Game(GameMap map, AntRepository antRepository) {
        this.map = map;
        this.antRepository = antRepository;
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
        map.getAllCells().forEach(cell -> cell.renew(currentTurn)); //for future resources
        map.getAllCells().forEach(Cell::manageResources);
        /*
         * if (isFinished()) { Colony winnerColony = gameJudge.getWinner();
         * this.graphicLogDTO.game_config.winner = winnerColony.getId();
         * this.graphicLogDTO.stats.winner = winnerColony.getId(); Log.i("Game",
         * "Game finished, winner colony id: " + winnerColony.getId()); System.exit(0);
         * }
         */
        generateTurnGraphicLog();
        currentTurn++;
    }

    private void generateTurnGraphicLog() {
        TurnDTO turnLog = new TurnDTO();
        turnLog.turn_num = currentTurn;
        turnLog.base0_health = this.getColonies().get(0).getBaseHealth();
        turnLog.base1_health = this.getColonies().get(1).getBaseHealth();
        List<ChatElementDTO> chat_box_0 = new ArrayList<>();
        this.getColonies().get(0).getChatBox().getChatMessages().forEach(
                (msg) -> chat_box_0.add(new ChatElementDTO(msg.getMessage(), msg.getValue(), msg.getSender_id())));
        List<ChatElementDTO> chat_box_1 = new ArrayList<>();
        this.getColonies().get(1).getChatBox().getChatMessages().forEach(
                (msg) -> chat_box_1.add(new ChatElementDTO(msg.getMessage(), msg.getValue(), msg.getSender_id())));
        turnLog.important_chat_box_0 = chat_box_0;
        turnLog.important_chat_box_1 = chat_box_1;
        List<CellDTO> cells = new ArrayList<>();
        for (Cell cell : map.getAllCells()) {
            cells.add(new CellDTO(cell));
        }
        turnLog.cells = cells;
        List<AttackSummary> attackSummaries = attackHandler.getAttackSummaries();
        List<AttackDTO> attacks = attackSummaries.stream()
                .map(x -> new AttackDTO(x.attacker_id, x.defender_id, x.src_row, x.src_col, x.dst_row, x.dst_col))
                .collect(Collectors.toList());
        turnLog.attacks = attacks;

        turnLog = addMoreToLog(turnLog, 0);
        turnLog = addMoreToLog(turnLog, 1);
        turnLog = addResourceLog(turnLog);

        List<ChatElementDTO> trivial_chat_box_0 = new ArrayList<>();
        if (getColony(0).getAllMessagesThisTurn() != null)
            getColony(0).getAllMessagesThisTurn().forEach((msg) -> trivial_chat_box_0
                    .add(new ChatElementDTO(msg.getMessage(), msg.getValue(), msg.getSender_id())));
        List<ChatElementDTO> trivial_chat_box_1 = new ArrayList<>();
        if (getColony(1).getAllMessagesThisTurn() != null)
            getColony(1).getAllMessagesThisTurn().forEach((msg) -> trivial_chat_box_1
                    .add(new ChatElementDTO(msg.getMessage(), msg.getValue(), msg.getSender_id())));
        turnLog.trivial_chat_box_0 = trivial_chat_box_0;
        turnLog.trivial_chat_box_1 = trivial_chat_box_1;
        getColony(0).setAllMessagesThisTurn(new ArrayList<>());
        getColony(1).setAllMessagesThisTurn(new ArrayList<>());

        graphicLogDTO.turns.add(turnLog);
    }

    private TurnDTO addResourceLog(TurnDTO log) {
        log.team0_current_resource0 = getColony(0).getThisTurnBread();
        log.team0_current_resource1 = getColony(0).getThisTurnGrass();
        log.team1_current_resource0 = getColony(1).getThisTurnBread();
        log.team1_current_resource1 = getColony(1).getThisTurnGrass();
        log.team0_total_resource0 = getColony(0).getGainedBread();
        log.team0_total_resource1 = getColony(0).getGainedGrass();
        log.team1_total_resource0 = getColony(1).getGainedBread();
        log.team1_total_resource1 = getColony(1).getGainedGrass();
        getColony(0).setThisTurnBread(0);
        getColony(0).setThisTurnGrass(0);
        getColony(1).setThisTurnBread(0);
        getColony(1).setThisTurnGrass(0);
        return log;
    }

    private TurnDTO addMoreToLog(TurnDTO log, int colonyID) {
        List<Ant> ants = getColony(colonyID).getAnts();
        int workers_alive = 0;
        int workers = 0;
        int soldier_alive = 0;
        int soldiers = 0;
        for (Ant ant : ants) {
            if (ant.getAntType() == AntType.SOLDIER) {
                soldiers++;
                if (!ant.isDead())
                    soldier_alive++;
            } else {
                workers++;
                if (!ant.isDead())
                    workers_alive++;
            }
        }
        if (colonyID == 0) {
            log.team0_alive_soldiers = soldier_alive;
            log.team0_alive_workers = workers_alive;
            log.team0_total_soldiers = getColonies().get(0).getAllSoldierAntsGeneratedCount();
            log.team0_total_workers = getColonies().get(0).getAllAntsGeneratedCount() - log.team0_total_soldiers;
        } else {
            log.team1_alive_soldiers = soldier_alive;
            log.team1_alive_workers = workers_alive;
            log.team1_total_soldiers = getColonies().get(1).getAllSoldierAntsGeneratedCount();
            log.team1_total_workers = getColonies().get(1).getAllAntsGeneratedCount() - log.team1_total_soldiers;
        }
        return log;
    }

    public GameJudge getGameJudge() {
        return gameJudge;
    }

    // remove dead ants messages (if client is alive!)
    private Map<String, List<ClientMessageInfo>> filterMessages(Map<String, List<ClientMessageInfo>> messages) {
        Map<String, List<ClientMessageInfo>> filteredMessages = new HashMap<>();
        for (String key : messages.keySet()) {
            filteredMessages.put(key, new ArrayList<>());
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
        colony.setAllMessagesThisTurn(chatMessages);
        if (GameHandler.showGameLog)
            System.out.println("chat message: " + Json.GSON.toJson(chatMessages));
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
        if (Game.quickResult.finished) {
            Game.quickResult.winnerID = getAntByID(Game.quickResult.antID).getColonyId();
            Game.quickResult.winnerID = (Game.quickResult.winnerID + 1) % 2;
            return true;
        }
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

    public AttackHandler getAttackHandler() {
        return attackHandler;
    }

    public AntRepository getAntRepository() {
        return antRepository;
    }

    // when one process fails, stop the game, declare winner
    public static class QuickResult {
        public int winnerID;
        public boolean finished = false;
        public int antID;

        public void antFailed(int antID) {
            finished = true;
            this.antID = antID;
        }
    }
}
