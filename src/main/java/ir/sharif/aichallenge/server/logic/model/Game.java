package ir.sharif.aichallenge.server.logic.model;

import ir.sharif.aichallenge.server.common.network.data.ClientMessageInfo;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.logic.handlers.AttackHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Major class to control a game.
 */
public class Game {
    private HashMap<Integer, Colony> colonyHashMap;
    private GameMap map;
    private int currentTurn = 1;
    private AttackHandler attackHandler;

    // messages to be sent to clients in this turn
    private Message[] clientTurnMessages;

    public static final int GAME_MAXIMUM_TURN_COUNT = 50;


    /**
     * Create a Game with specific GameMap and Handlers.
     *
     * @param map The gameMap of the Game.
     *            //     * @param ownerHandler Handles all stuff about owner of nodes.
     *            //     * @param turnHandler  Handles turn logic of the game.
     *            //     * @param scoreHandler The scoring system.
     *            //     * @param validator    The validator for players actions.
     */
    public Game(GameMap map) {
        this.map = map;
        attackHandler = new AttackHandler(map, colonyHashMap);
    }

    public void moveAnt(int colonyId, int antId, MoveType moveType) {
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
        //      increment turn
        //      generate messages for clients and add them to [clientTurnMessages]
        //      set [isGameFinished] when necessary
        //      and many many other things :)
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
}
