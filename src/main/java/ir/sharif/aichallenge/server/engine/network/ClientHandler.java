package ir.sharif.aichallenge.server.engine.network;

import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.common.network.JsonSocket;
import ir.sharif.aichallenge.server.common.network.data.ClientMessage;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.common.network.data.MessageTypes;
import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.logic.model.Game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * {@link ClientHandler} handles a client, i.e. it is responsible for
 * sending/receiving messages to/from the client.
 * <p>
 * After that a client is verified by the engine, engine assigns a
 * {@link ClientHandler} to that client.
 * <p>
 * Messages should arrive in a predefined interval of time (which is determined
 * by the engine). Every message arrived out of this interval is discarded
 * immediately. If two or more messages arrived in the valid time the last one
 * is stored as the "last validated message".
 */
public class ClientHandler {

    /**
     * Maximum number of exceptions during connection.
     */
    public static final int MAX_NUM_EXCEPTIONS = 3;

    /**
     * Logging tag.
     */
    private static final String TAG = "ClientHandler";

    private final String logTag;

    private int id;

    /**
     * Socket of the client.
     */
    private JsonSocket client;

    /**
     * Lock for {@link #client}.
     */
    private final Object clientLock;

    /**
     * Termination flag.
     */
    private boolean sendTerminateFlag;

    /**
     * Termination flag.
     */
    private boolean receiveTerminateFlag;

    /**
     * Last valid message which is arrived on time.
     */
    private final ArrayList<ClientMessage> receivedMessages;

    /**
     * Last message received from client.
     */
    private ClientMessage lastReceivedMessage;

    /**
     * This object is notified when a message is received.
     */
    private final Object messageNotifier;

    private int maxSendingFails = 0;

    /**
     * Message queue. These messages will be sent to the client asap.
     */
    private final LinkedBlockingDeque<Message> messagesToSend;

    /**
     * Message queue.
     */
    private final ArrayList<Message> messagesQueued;

    /**
     * Number of exceptions occurred during communication.
     */
    private int numOfExceptions;

    /**
     * Current Turn in game
     */
    private AtomicInteger currentTurn;

    /**
     * Simulate Thread Semaphore
     */
    private Semaphore simulationSemaphore;

    /**
     * True if client has finished his/her current turn
     */
    private AtomicBoolean endReceived;

    /**
     * True if client is not active anymore (is dead for example)
     */
    private AtomicBoolean isActive;

    private ClientNetwork network;

    public void setNetwork(ClientNetwork network) {
        this.network = network;
    }

    public ClientHandler(int id, Semaphore simulationSemaphore, AtomicInteger currentTurn, AtomicBoolean endReceived,
            AtomicBoolean isActive) {
        this.id = id;
        messagesToSend = new LinkedBlockingDeque<>();
        receivedMessages = new ArrayList<>();
        messagesQueued = new ArrayList<>();
        clientLock = new Object();
        messageNotifier = new Object();

        this.simulationSemaphore = simulationSemaphore;
        this.currentTurn = currentTurn;
        this.endReceived = endReceived;
        this.isActive = isActive;

        this.logTag = TAG + this.id;
    }

    /**
     * Queues a message for the client. Message is not sent until {@link #send} is
     * called.
     *
     * @param msg message to send.
     */
    public void queue(Message msg) {
        synchronized (messagesQueued) {
            messagesQueued.add(msg);
        }
    }

    /**
     * Sends all queued messages (non-blocking).
     */
    public void send() {
        synchronized (messagesQueued) {
            messagesToSend.addAll(messagesQueued);
            messagesQueued.clear();
        }
    }

    /**
     * Returns a runnable which sends messages when it is ran.
     */
    public Runnable getSender() {
        return () -> {
            while (!sendTerminateFlag) {
                try {
                    Thread.sleep(0);
                    Message msg = null;
                    try {
                        msg = messagesToSend.take();
                    } catch (InterruptedException ignored) {

                    }
                    if (sendTerminateFlag)
                        return;
                    if (msg == null)
                        continue;
                    // System.out.println("client id: " + id + " msg: " + Json.GSON.toJson(msg));
                    client.send(msg);
                } catch (Exception e) {
                    Log.i(logTag, "Message sending failure", e);
                    maxSendingFails++;
                    if (maxSendingFails > MAX_NUM_EXCEPTIONS) {
                        Log.e("ClientHandler", "Max number of sending failure expections reached " + id);
                        Game.quickResult.antFailed(id);
                    }
                }
            }
        };
    }

    /**
     * A message is valid if it is arrived in a valid time, which is determined by
     * the engine.
     *
     * @return last validated message.
     * @see #getReceiver
     */
    public List<ClientMessage> getReceivedMessages() {
        ArrayList<ClientMessage> messages;
        synchronized (receivedMessages) {
            messages = new ArrayList<>(receivedMessages);
            receivedMessages.clear();
        }
        return messages;
    }

    /**
     * Binds handler to a socket, i.e. this handler is responsible for
     * sending/receiving messages to/from the socket.
     *
     * @param socket client
     */
    public void bind(JsonSocket socket) {
        try {
            if (client != null)
                client.close();
        } catch (IOException e) {
            Log.i(logTag, "socket closing failure", e);
            handleIOE(e);
        } finally {
            synchronized (clientLock) {
                client = socket;
                clientLock.notifyAll();
            }
        }
    }

    /**
     * The result of method is a {@link Runnable} object. When this runnable is
     * called it receives a new message from the client and if it arrives in a valid
     * time (which is checked using <code>timeValidator</code>) stores it in
     * {@link #receivedMessages}.
     *
     * @param timeValidator <code>get</code> method of this object returns true if
     *                      and only if it is called in a valid time. (valid time is
     *                      the time when messages can be arrived from clients, e.g.
     *                      half a second after each turn)
     * @return a runnable which is used by engine to receive new messages of client
     */
    public Runnable getReceiver(Supplier<Boolean> timeValidator) {
        return () -> {
            while (!receiveTerminateFlag) {
                try {
                    if (!isActive.get() && !endReceived.get() && timeValidator.get()) {
                        simulationSemaphore.release();
                        endReceived.set(true);
                        Log.i(logTag, "Not waiting for messages because player is not active.");
                    }
                    if (endReceived.get())
                        continue;
                    receive();
                    if (!timeValidator.get() || lastReceivedMessage == null)
                        continue;

                    if (lastReceivedMessage.getTurn() != currentTurn.get()) // Invalid message
                    {
                        Log.i(logTag, "Message received late.");
                        continue;
                    }

                    if (lastReceivedMessage.getType().equals(MessageTypes.END_TURN)) {
                        if (!endReceived.get()) {
                            simulationSemaphore.release();
                            endReceived.set(true);
                        }
                        continue; // skipping end message
                    }

                    synchronized (receivedMessages) {
                        receivedMessages.add(lastReceivedMessage);
                    }

                } catch (IOException e) {
                    Log.i(logTag, "message receiving failure");
                    handleIOE(e);
                } catch (Exception e) {
                    Log.i(logTag, "message receiving failure", e);
                }
            }
            if (!endReceived.get())
                simulationSemaphore.release();
            Log.i(logTag, String.format("Client Terminated with id: %d", id));
            if (!network.deadIDs.contains(id)) {
                network.deadIDs.add(id);
            }
        };
    }

    /**
     * Receives a message from client.
     *
     * @throws IOException if an I/O error occurs.
     */
    private void receive() throws IOException, InterruptedException {
        Thread.sleep(0);
        lastReceivedMessage = null;
        if (receiveTerminateFlag)
            return;
        client.id = getId();
        lastReceivedMessage = client.get(ClientMessage.class);
        synchronized (messageNotifier) {
            messageNotifier.notifyAll();
        }
    }

    /**
     * Returns true if any client is connected to this client handler.
     *
     * @return true if any client is connected to this client handler.
     */
    public boolean isConnected() {
        return client != null;
    }

    /**
     * Blocks caller method until the client send a message.
     *
     * @throws InterruptedException if current thread is interrupted.
     */
    public void waitForClientMessage() throws InterruptedException {
        synchronized (messageNotifier) {
            messageNotifier.wait();
        }
    }

    /**
     * Blocks caller method at most <code>timeout</code> milliseconds until the
     * client send a message.
     *
     * @throws InterruptedException if current thread is interrupted.
     */
    public void waitForClientMessage(long timeout) throws InterruptedException {
        synchronized (messageNotifier) {
            messageNotifier.wait(timeout);
        }
    }

    /**
     * Blocks caller method until a client is connected to this handler.
     *
     * @throws InterruptedException if current thread is interrupted.
     */
    public void waitForClient() throws InterruptedException {
        if (client == null || client.isClosed())
            synchronized (clientLock) {
                clientLock.wait();
            }
    }

    /**
     * Blocks caller method at most <code>timeout</code> milliseconds until a client
     * is connected to this handler.
     *
     * @param timeout timeout in milliseconds
     * @throws InterruptedException if current thread is interrupted.
     */
    public void waitForClient(long timeout) throws InterruptedException {
        if (client == null || client.isClosed())
            synchronized (clientLock) {
                clientLock.wait(timeout);
            }
    }

    /**
     * Terminates operations of the handler. It actually closes the socket and
     * changes a flag.
     */
    public void terminate() {
        sendTerminateFlag = true;
        receiveTerminateFlag = true;
        if (client != null)
            try {
                client.close();
            } catch (IOException ignored) {
            }
        client = null;
    }

    /**
     * Terminates sending messages.
     */
    public void terminateSending() {
        sendTerminateFlag = true;
    }

    /**
     * Terminates receiving messages.
     */
    public void terminateReceiving() {
        receiveTerminateFlag = true;
    }

    /**
     * Handles I/O Exceptions.
     *
     * @param e exception
     */
    private void handleIOE(IOException e) {
        numOfExceptions++;
        if (numOfExceptions > MAX_NUM_EXCEPTIONS) {
            terminate();
        }
    }

    public int getId() {
        return id;
    }
}
