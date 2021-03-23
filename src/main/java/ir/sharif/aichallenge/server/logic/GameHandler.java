package ir.sharif.aichallenge.server.logic;

import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.common.network.data.ActionInfo;
import ir.sharif.aichallenge.server.common.network.data.ClientMessageInfo;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.common.network.data.MessageTypes;
import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.engine.core.GameLogic;
import ir.sharif.aichallenge.server.logic.config.ConfigReader;
import ir.sharif.aichallenge.server.logic.dto.graphics.GraphicGameConfigDTO;
import ir.sharif.aichallenge.server.logic.dto.payloads.GameConfigDTO;
import ir.sharif.aichallenge.server.logic.dto.payloads.GameStatusDTO;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.model.Game;
import ir.sharif.aichallenge.server.logic.model.Colony.Colony;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.cell.CellType;
import ir.sharif.aichallenge.server.logic.model.chatbox.ChatMessage;
import ir.sharif.aichallenge.server.logic.model.map.MapGenerator;
import ir.sharif.aichallenge.server.logic.model.map.MapGenerator.MapGeneratorResult;
import ir.sharif.aichallenge.server.logic.utility.AntGenerator;
import ir.sharif.aichallenge.server.logic.utility.GraphicUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import javax.naming.spi.DirStateFactory.Result;

import com.google.gson.JsonObject;

public class GameHandler implements GameLogic {

    private Game game;
    private Integer antsNum;
    private ArrayList<Integer> deads = new ArrayList<>();
    private boolean newAntsCreated = false;
    private List<Integer> newAntIDs = new ArrayList<>();
    public static int initialAntsNum;
    public static boolean showGameLog = false;
    // if one colony generated its ants
    private static boolean oneColonyGeneratedAnt = false;
    private static boolean thereIsQueuedColony = false;

    public GameHandler() {
        this.antsNum = 0;
    }

    @Override
    public int getClientsNum() {
        return antsNum;
    }

    @Override
    public boolean[] getActiveClients() {
        boolean[] bitArray = new boolean[antsNum];
        for (int i = 0; i < antsNum; i++) {
            bitArray[i] = game.isAntAlive(i);
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
        // read config file
        ConfigReader.readConfigFile();
        // generate map
        MapGeneratorResult generatedMap = MapGenerator.generateRandomMap();
        // create Game
        this.game = new Game(generatedMap.map, generatedMap.colonies);
        this.game.graphicLogDTO.game_config = new GraphicGameConfigDTO(generatedMap.map);

        /*
         * antsNum = initialAntsNum; ArrayList<Ant> initialAnts = new ArrayList<>(); for
         * (int i = 0; i < antsNum; i++) { int colonyID = (i < (antsNum / 2)) ? 0 : 1;
         * initialAnts.add(new Ant(i, colonyID,
         * generatedMap.colonies.get(colonyID).getBase().getX(),
         * generatedMap.colonies.get(colonyID).getBase().getY(), ((i % 2) == 1) ?
         * AntType.WORKER : AntType.SOLDIER)); } try { for (Ant ant : initialAnts) {
         * game.addAntToGame(ant, ant.getColonyId()); } } catch (GameActionException e)
         * { System.out.println("Can't add ants to game!"); e.printStackTrace(); }
         */

        game.getColonies().get(0).setToBeGeneratedSoldiersCount(initialAntsNum / 4);
        game.getColonies().get(0).setToBeGeneratedWorkersCount(initialAntsNum / 4);

        game.getColonies().get(1).setToBeGeneratedSoldiersCount(initialAntsNum / 4);
        game.getColonies().get(1).setToBeGeneratedWorkersCount(initialAntsNum / 4);
        thereIsQueuedColony = true;

        /*
         * for (Ant ant : initialAnts) { AntGenerator.runNewAnt(ant.getAntType(),
         * ant.getId(), ant.getColonyId()); }
         */

    }

    @Override
    public Message getUIInitialMessage() {
        return null;
    }

    @Override
    public Message[] getClientInitialMessages() {
        Message[] initialMessages = new Message[antsNum];

        // send game config to Ants!
        for (int i = 0; i < antsNum; i++) {
            initialMessages[i] = new Message(MessageTypes.INIT,
                    Json.GSON.toJsonTree(new GameConfigDTO(this.game, i), GameConfigDTO.class).getAsJsonObject());
        }

        System.out.println("initial messages created");
        return initialMessages;
    }

    @Override
    public ArrayList<Integer> simulateEvents(Map<String, List<ClientMessageInfo>> messages) {
        oneColonyGeneratedAnt = false;
        ArrayList<Integer> result = new ArrayList<>();
        if (thereIsQueuedColony) {
            // System.out.println("1000");
            result = handleAntGeneration();
            // System.out.println("result: " + Json.GSON.toJson(result));
            return result;
        } else {
            // System.out.println("1001");
            game.passTurn(messages);
            result = handleAntGeneration();
            if (showGameLog)
                showMap(true);
            else
                System.out.println("turn passed");
            // System.out.println("result: " + Json.GSON.toJson(result));
            return result;
        }
    }

    private ArrayList<Integer> handleAntGeneration() {
        ArrayList<Integer> result = new ArrayList<>();
        Colony firstCol = game.getColony(0);
        Colony secondCol = game.getColony(1);
        int soldiers = firstCol.getToBeGeneratedSoldiersCount();
        int workers = firstCol.getToBeGeneratedWorkersCount();
        if (soldiers > 0 || workers > 0) {
            if (soldiers > 0) {
                result.add(addNewAnt(firstCol.getBase().getX(), firstCol.getBase().getY(), firstCol.getId(),
                        AntType.SOLDIER));
                firstCol.setToBeGeneratedSoldiersCount(soldiers - 1);
                // System.out.println("1003");
            } else if (workers > 0) {
                // System.out.println("1005");
                result.add(addNewAnt(firstCol.getBase().getX(), firstCol.getBase().getY(), firstCol.getId(),
                        AntType.WORKER));
                firstCol.setToBeGeneratedWorkersCount(workers - 1);
            }
        } else {
            soldiers = secondCol.getToBeGeneratedSoldiersCount();
            workers = secondCol.getToBeGeneratedWorkersCount();
            if (soldiers > 0 || workers > 0) {
                if (soldiers > 0) {
                    result.add(addNewAnt(secondCol.getBase().getX(), secondCol.getBase().getY(), secondCol.getId(),
                            AntType.SOLDIER));
                    secondCol.setToBeGeneratedSoldiersCount(soldiers - 1);
                    // System.out.println("1003");
                } else if (workers > 0) {
                    // System.out.println("1005");
                    result.add(addNewAnt(secondCol.getBase().getX(), secondCol.getBase().getY(), secondCol.getId(),
                            AntType.WORKER));
                    secondCol.setToBeGeneratedWorkersCount(workers - 1);
                }
            }
        }
        if (firstCol.getToBeGeneratedSoldiersCount() > 0 || firstCol.getToBeGeneratedWorkersCount() > 0
                || secondCol.getToBeGeneratedSoldiersCount() > 0 || secondCol.getToBeGeneratedWorkersCount() > 0) {
            thereIsQueuedColony = true;
        } else {
            thereIsQueuedColony = false;
        }

        /*
         * for (Colony colony : game.getColonies()) { if (!oneColonyGeneratedAnt) {
         * System.out.println("1002"); int soldiers =
         * colony.getToBeGeneratedSoldiersCount(); if (soldiers > 0) {
         * result.add(addNewAnt(colony.getBase().getX(), colony.getBase().getY(),
         * colony.getId(), AntType.SOLDIER));
         * colony.setToBeGeneratedSoldiersCount(soldiers - 1);
         * System.out.println("1003"); oneColonyGeneratedAnt = true; } int workers; if
         * (!oneColonyGeneratedAnt) { System.out.println("1004"); workers =
         * colony.getToBeGeneratedWorkersCount(); if (workers > 0) {
         * System.out.println("1005");
         * 
         * result.add(addNewAnt(colony.getBase().getX(), colony.getBase().getY(),
         * colony.getId(), AntType.WORKER)); colony.setToBeGeneratedWorkersCount(workers
         * - 1); oneColonyGeneratedAnt = true; } } soldiers =
         * colony.getToBeGeneratedSoldiersCount(); workers =
         * colony.getToBeGeneratedWorkersCount(); if (soldiers == 0 && workers == 0) {
         * thereIsQueuedColony = false; System.out.println("1006");
         * 
         * } else { thereIsQueuedColony = true; System.out.println("1007");
         * 
         * } } else { int soldiers = colony.getToBeGeneratedSoldiersCount(); int workers
         * = colony.getToBeGeneratedWorkersCount(); if ((soldiers > 0) || (workers > 0))
         * { System.out.println("1008"); thereIsQueuedColony = true; } if (soldiers == 0
         * && workers == 0) { thereIsQueuedColony = false; System.out.println("1009");
         * 
         * } }
         */
        // }

        return result;
    }

    private int addNewAnt(int x, int y, int colonyID, AntType type) {
        antsNum++;
        int id = antsNum - 1;
        newAntsCreated = true;
        newAntIDs.add(id);
        Ant ant = new Ant(id, colonyID, x, y, type);
        try {
            game.addAntToGame(ant, colonyID);
        } catch (GameActionException e) {
            e.printStackTrace();
        }
        AntGenerator.runNewAnt(type, id, colonyID);
        return id;
    }

    private void showMap(boolean showChatbox) {
        System.out.println("--------this turn: " + (game.getTurn() - 1) + "--------");
        for (Cell cell : game.getMap().getAllCells()) {
            System.out.println("[" + cell.getX() + "," + cell.getY() + "]: " + cell.getCellType().toString() + " "
                    + cell.getResourceType().toString() + ":" + cell.getResourceAmount() + " --> "
                    + getAntsIds(cell.getAnts()));
        }
        System.out.println();
        if (showChatbox) {
            for (Colony colony : game.getColonies()) {
                System.out.println("chatbox for colony: " + colony.getId() + " with health: " + colony.getBaseHealth()
                        + " bread:" + colony.getGainedBread() + " grass:" + colony.getGainedGrass());
                for (ChatMessage message : colony.getChatBox().getChatMessages()) {
                    System.out.println(Json.GSON.toJson(message, ChatMessage.class));
                }
            }
        }
        System.out.println();
    }

    private String getAntsIds(List<Ant> ants) {
        String result = "";
        for (Ant a : ants) {
            result += " [" + Json.GSON.toJson(a, Ant.class) + "] ";
        }
        return result;
    }

    @Override
    public void generateOutputs() {
        // AIC 2019 - Graphic
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

        // dead ants
        HashMap<Integer, Ant> deadAnts = game.getNewDeadAnts();

        // Send game status to each ant
        Message[] messages = new Message[antsNum];
        if (thereIsQueuedColony) {
            return messages;
        }
        for (int i = 0; i < antsNum; i++) {
            if (deadAnts != null && deadAnts.keySet().contains(i)) {
                messages[i] = new Message(MessageTypes.KILL, new JsonObject());
                deads.add(i);
            } else {
                if (!deads.contains(i)) {
                    if (newAntsCreated && newAntIDs.contains(i)) {
                        messages[i] = new Message(MessageTypes.INIT, Json.GSON
                                .toJsonTree(new GameConfigDTO(this.game, i), GameConfigDTO.class).getAsJsonObject());
                    } else {
                        messages[i] = new Message(MessageTypes.GAME_STATUS, Json.GSON
                                .toJsonTree(new GameStatusDTO(this.game, i), GameStatusDTO.class).getAsJsonObject());
                    }
                }
            }
        }
        // System.out.println("1010");

        newAntsCreated = false;
        return messages;
    }

    @Override
    public Message[] getClientEndMessages() {
        // no need to send message at end now!
        return new Message[0];
    }

    @Override
    public boolean isGameFinished() {
        if (game.isFinished()) {
            Colony winner = game.getGameJudge().getWinner();
            Log.i("Game Finished", "Winner Colony ID: " + winner.getId());
            Log.i("Killer!", "Killing ants... [if not worked, kill them by hand :)]");
            AntGenerator.killAnts();
            GraphicUtils.generateLogFile(game.graphicLogDTO);
        }
        return game.isFinished();
    }

    @Override
    public void terminate() {
        // terminate some graphic things for example!
        // produce some logs if necessary
    }
}
