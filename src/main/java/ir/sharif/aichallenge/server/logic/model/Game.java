package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.common.network.data.*;
import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.handlers.AttackHandler;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.ant.MoveType;
import ir.sharif.aichallenge.server.logic.model.cell.CellType;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;
import ir.sharif.aichallenge.server.logic.model.chatbox.ChatMessage;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;

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
    private HashMap<String, Ant> antHashMap;
    private GameMap map;
    public int currentTurn = 0;
    private AttackHandler attackHandler;

    // messages to be sent to clients in this turn
    private Message[] clientTurnMessages;
    private HashMap<String, Ant> newDeadAnts;

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
        attackHandler = new AttackHandler(map, colonyHashMap);
    }

    public void moveAnt(int colonyId, String antId, int moveType) {
        Ant ant = colonyHashMap.get(colonyId).getAnt(antId);
        int newX = ant.getXPosition();
        int newY = ant.getYPosition();
        switch (moveType) {
            case MoveType.UP:
                newY -= 1;
                break;
            case MoveType.DOWN:
                newY += 1;
                break;
            case MoveType.LEFT:
                newX -= 1;
                break;
            case MoveType.RIGHT:
                newX += 1;
                break;
            default:
                return;
        }
        newX = newX % map.getWidth();
        newY = newY % map.getHeight();

        if (map.getCell(newX, newY).cellType == CellType.WALL)
            return;

        moveAnt(ant, newX, newY);
    }

    private void moveAnt(Ant ant, int newX, int newY) {
        map.getCell(ant.getXPosition(), ant.getYPosition()).RemoveAnt(ant);
        ant.moveTo(newX, newY);
        map.getCell(newX, newY).addAnt(ant);
    }

    private void addMessage(int colonyId, List<ClientMessageInfo> messages) {
        List<ChatMessage> chatMessages = messages.stream()
                .map(x -> (SendMessageInfo) (x))
                .map(x -> new ChatMessage(x.getMessage(), x.getValue(), currentTurn))
                .collect(Collectors.toList());
        colonyHashMap.get(colonyId).getChatBox().addMessage(chatMessages);
    }

    public void passTurn(Map<String, List<ClientMessageInfo>> messages) {
        // TODO: based on the messages in MessageTypes, do changes in game
        // for example:
        // increment turn
        // generate messages for clients and add them to [clientTurnMessages]
        // set [isGameFinished] when necessary
        // and many many other things :)
        for (Colony colony : colonyHashMap.values()) {
            for (Ant ant : colony.getAnts()) {
                attackHandler.runAttack(ant);
            }
        }
        handleDeadAnts();

        for (String messageType : messages.keySet()) {
            messages.get(messageType).removeIf(x -> newDeadAnts.containsKey(x.getPlayerId()));
        }

        //TODO change playerId type to String in ClientMessageInfo
        Map<Integer, List<ClientMessageInfo>> groupedSendMessages = messages
                .getOrDefault(MessageTypes.SEND_MESSAGE, new ArrayList<>())
                .stream().collect(Collectors.groupingBy(x -> antHashMap.get(x.getPlayerId()).getColonyId()));
        for (Integer colonyId : groupedSendMessages.keySet()) {
            addMessage(colonyId, groupedSendMessages.get(colonyId));
        }

        List<ActionInfo> actionMessages = messages
                .getOrDefault(MessageTypes.ACTION, new ArrayList<>())
                .stream().map(x -> ((ActionInfo) (x))).collect(Collectors.toList());
        for (ActionInfo actionMessage : actionMessages) {
            Ant ant = antHashMap.get(actionMessage.getPlayerId());
            moveAnt(ant.getColonyId(), ant.getId(), actionMessage.getDirection());
        }

        map.handleNewActions();

        currentTurn++;
    }

    private void handleDeadAnts() {
        newDeadAnts = new HashMap<>();
        for (Ant ant : antHashMap.values()) {
            if (!ant.isDead())
                continue;
            colonyHashMap.get(ant.getColonyId()).removeAnt(ant.getId());
            map.getCell(ant.getXPosition(), ant.getYPosition()).removeAnt(ant);
            antHashMap.remove(ant.getId());
            newDeadAnts.put(ant.getId(), ant);
            if (ant.getAntType() == AntType.SOLDIER) {
                map.addResource(ResourceType.GRASS, ConstConfigs.RATE_DEATH_RESOURCE, ant.getXPosition(), ant.getYPosition());
            } else {
                if (ant.getResourceType() == ResourceType.NONE)
                    map.addResource(ResourceType.BREAD, ConstConfigs.RATE_DEATH_RESOURCE, ant.getXPosition(), ant.getYPosition());
                else if (ant.getResourceType() == ResourceType.BREAD)
                    map.addResource(ResourceType.BREAD, ConstConfigs.RATE_DEATH_RESOURCE + ant.getResourceAmount()
                            , ant.getXPosition(), ant.getYPosition());
                else {
                    map.addResource(ResourceType.BREAD, ConstConfigs.RATE_DEATH_RESOURCE, ant.getXPosition(), ant.getYPosition());
                    map.addResource(ResourceType.GRASS, ant.getResourceAmount(), ant.getXPosition(), ant.getYPosition());
                }
            }
        }
    }

    public int getTurn() {
        return currentTurn;
    }

    public Message[] getClientTurnMessages() {
        return new Message[0];
    }

    public boolean isFinished() {
        return false;
    }

    public boolean isAntAlive(String antId) {
        for (Integer colonyID : colonyHashMap.keySet()) {
            Ant ant = colonyHashMap.get(colonyID).getAnt(antId);
            if (ant != null) {
                return !ant.isDead();
            }
        }
        return false;
    }

    public void addAntToGame(Ant ant, Integer colonyId) throws GameActionException {
        Colony colony = colonyHashMap.get(colonyId);
        if (colony == null) {
            throw new ColonyNotExistsException("", colonyId);
        }
        colony.addNewAnt(ant);
    }

    public Ant getAntByID(String antId) {
        for (Integer colId : colonyHashMap.keySet()) {
            Ant ant = colonyHashMap.get(colId).getAnt(antId);
            if (ant != null)
                return ant;
        }
        // not exists
        return null;
    }

    public GameMap getMap() {
        return map;
    }

    public Colony getColony(int colonyId) {
        return colonyHashMap.get(colonyId);
    }
}
