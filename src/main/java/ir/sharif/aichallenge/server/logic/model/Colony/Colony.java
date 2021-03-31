package ir.sharif.aichallenge.server.logic.model.Colony;

import ir.sharif.aichallenge.server.logic.config.ConstConfigs;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.GameActionException;
import ir.sharif.aichallenge.server.logic.handlers.exceptions.InvalidAntForColonyException;
import ir.sharif.aichallenge.server.logic.model.ant.Ant;
import ir.sharif.aichallenge.server.logic.model.ant.AntType;
import ir.sharif.aichallenge.server.logic.model.cell.BaseCell;
import ir.sharif.aichallenge.server.logic.model.cell.Cell;
import ir.sharif.aichallenge.server.logic.model.cell.ResourceType;
import ir.sharif.aichallenge.server.logic.model.chatbox.ChatBox;
import ir.sharif.aichallenge.server.logic.model.chatbox.ChatMessage;

import java.util.HashMap;
import java.util.List;

public class Colony {
    private static Integer colonyUUID = 1000;

    private int id;
    private int baseAttackerId;
    private int gainedBread = 0;
    private int gainedGrass = 0;
    private int thisTurnBread = 0;
    private int thisTurnGrass = 0;
    private Cell base;
    private int baseHealth;
    private HashMap<Integer, Ant> ants;
    private ChatBox chatBox;
    private int toBeGeneratedWorkersCount;
    private int toBeGeneratedSoldiersCount;
    private int allWorkerAntsGeneratedCount;
    private int allSoldierAntsGeneratedCount;
    private List<ChatMessage> allMessagesThisTurn;

    public Colony(int id,int baseAttackerId, BaseCell base, int baseHealth) {
        this(id,baseAttackerId);
        this.base = base;
        this.baseHealth = baseHealth;
    }

    Colony(int id, int baseAttackerId) {
        this.id = id;
        chatBox = new ChatBox();
        this.baseAttackerId = baseAttackerId;
        addBread(ConstConfigs.COLONY_INITIAL_BREAD);
        addGrass(ConstConfigs.COLONY_INITIAL_GRASS);
        ants = new HashMap<>();
        allSoldierAntsGeneratedCount = 0;
        allWorkerAntsGeneratedCount = 0;
    }

    void setBaseCell(BaseCell baseCell, int initialBaseHealth) {
        base = baseCell;
        baseHealth = initialBaseHealth;
    }

    public int getGainedBread() {
        return gainedBread;
    }

    public List<ChatMessage> getAllMessagesThisTurn() {
        return allMessagesThisTurn;
    }

    public void setAllMessagesThisTurn(List<ChatMessage> allMessagesThisTurn) {
        this.allMessagesThisTurn = allMessagesThisTurn;
    }

    public int getGainedGrass() {
        return gainedGrass;
    }

    public void setToBeGeneratedSoldiersCount(int toBeGeneratedSoldiersCount) {
        this.toBeGeneratedSoldiersCount = toBeGeneratedSoldiersCount;
    }

    public void setToBeGeneratedWorkersCount(int toBeGeneratedWorkersCount) {
        this.toBeGeneratedWorkersCount = toBeGeneratedWorkersCount;
    }

    public void addNewAnt(Ant ant) throws GameActionException {
        if (ant.getColonyId() != this.id) {
            throw new InvalidAntForColonyException("");
        }
        if (ant.getAntType() == AntType.WORKER)
            allWorkerAntsGeneratedCount++;
        else
            allSoldierAntsGeneratedCount++;
        this.ants.put(ant.getId(), ant);
    }

    private void addBread(int amount) {
        gainedBread += amount;
        thisTurnBread += amount;
        if (gainedBread >= ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT) {
            generateWorker();
        }
    }

    public int getThisTurnBread() {
        return thisTurnBread;
    }

    public int getThisTurnGrass() {
        return thisTurnGrass;
    }

    public void setThisTurnBread(int thisTurnBread) {
        this.thisTurnBread = thisTurnBread;
    }

    public void setThisTurnGrass(int thisTurnGrass) {
        this.thisTurnGrass = thisTurnGrass;
    }

    private void addGrass(int amount) {
        gainedGrass += amount;
        thisTurnGrass += amount;
        if (gainedGrass >= ConstConfigs.GENERATE_SOLDIER_GRASS_AMOUNT) {
            generateSoldier();
        }
    }

    public void addResource(ResourceType resourceType, int amount) {
        if (resourceType == ResourceType.GRASS)
            addGrass(amount);
        else
            addBread(amount);
    }

    private void generateSoldier() {
        int count = Math.floorDiv(gainedGrass, ConstConfigs.GENERATE_SOLDIER_GRASS_AMOUNT);
        toBeGeneratedSoldiersCount += count;
        gainedGrass -= count * ConstConfigs.GENERATE_SOLDIER_GRASS_AMOUNT;
    }

    private void generateWorker() {
        int count = Math.floorDiv(gainedBread, ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT);
        toBeGeneratedWorkersCount += count;
        gainedBread -= count * ConstConfigs.GENERATE_WORKER_BREAD_AMOUNT;
    }

    public int getId() {
        return id;
    }

    public Ant getAnt(int antId) {
        return ants.get(antId);
    }

    public List<Ant> getAnts() {
        return List.copyOf(ants.values());
    }

    public ChatBox getChatBox() {
        return chatBox;
    }

    public Cell getBase() {
        return base;
    }

    public void decreaseBaseHealth(int amount) {
        baseHealth -= amount;
    }

    public static Integer generateNewID() {
        return colonyUUID++;
    }

    public void removeAnt(int id) {
        ants.remove(id);
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    public int getToBeGeneratedWorkersCount() {
        return toBeGeneratedWorkersCount;
    }

    public int getToBeGeneratedSoldiersCount() {
        return toBeGeneratedSoldiersCount;
    }

    public int getAllSoldierAntsGeneratedCount() {
        return allSoldierAntsGeneratedCount;
    }

    public int getAllAntsGeneratedCount() {
        return allSoldierAntsGeneratedCount + allWorkerAntsGeneratedCount;
    }

    public int getAllResourcesAmount() {
        return gainedBread + gainedGrass;
    }

    public int getBaseAttackerId() {
        return baseAttackerId;
    }
}
