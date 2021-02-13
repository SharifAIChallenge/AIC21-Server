package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.common.network.data.ClientMessageInfo;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.logic.handlers.AttackHandler;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.ColonyNotExistsException;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.MoveType;
import ir.sharif.aichallenge.server.logic.model.cell.CellType;
import ir.sharif.aichallenge.server.logic.model.map.GameMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Major class to control a game.
 */
public class Game {
    // maps colony ids to colony
    private HashMap<Integer, Colony> colonyHashMap;
    private GameMap map;
    public int currentTurn = 0;
    private AttackHandler attackHandler;

    // messages to be sent to clients in this turn
    private Message[] clientTurnMessages;

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

    public void moveAnt(int colonyId, String antId, MoveType moveType) {
        Ant ant = colonyHashMap.get(colonyId).getAnt(antId);
        int newX = ant.getXPosition();
        int newY = ant.getYPosition();
        switch (moveType) {
            case UP:
                newY -= 1;
                break;
            case DOWN:
                newY += 1;
                break;
            case LEFT:
                newX -= 1;
                break;
            case RIGHT:
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
        map.getCell(newX, newY).AddAnt(ant);
    }

    public void addMessage(int colonyId, String message, int value) {

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

        currentTurn++;
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

    public Ant getAntByID (String antId) {
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
