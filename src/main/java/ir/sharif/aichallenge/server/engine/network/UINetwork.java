package ir.sharif.aichallenge.server.engine.network;

import ir.sharif.aichallenge.server.common.network.JsonSocket;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.common.network.data.MessageTypes;
import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.engine.config.Configs;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link UINetwork} is a engine which is responsible for sending
 * UI data to the <code>node.js</code> client.
 * <p>
 * When a client is connected to the engine, it sends a token and waits for the
 * initial message which contains necessary data of beginning of the game.
 * <p>
 * Messages are sent using methods {@link #sendBlocking} or {@link #sendNonBlocking}.
 * <p>
 * The communications are one sided, i.e. everything which is sent by client is
 * ignored by the engine.
 */
public final class UINetwork extends NetServer {

    // Logging tag
    private static final String TAG = "UINetwork";

    // UI token
    private final String mToken;

    // Current connection to UI
    private JsonSocket mClient;

    // Lock for {@link #mClient}
    private final Lock mClientLock;

    // Notifies waiters when a new client is connected
    private final Object clientNotifier;

    // Thread executor which is used to accept clients
    private ExecutorService executor;

    // Thread executor which is used to send messages
    private ExecutorService sendExecutor;

    /**
     * Initializes the class and starts sending messages to clients.
     * If there is no client at the time of sending, the message will be
     * thrown away.
     *
     * @see #sendBlocking
     * @see #sendNonBlocking
     * @see #hasClient
     * @see #waitForClient
     * @see #waitForNewClient
     */
    public UINetwork() {
        mToken = Configs.PARAM_UI_TOKEN.getValue();
        clientNotifier = new Object();
        mClientLock = new ReentrantLock(true);
    }

    /**
     * Sends a message to the client.
     * Caller method will be blocked until the message is sent.
     *
     * @param msg message to send
     */
    public void sendBlocking(Message msg) {
        try {
            mClient.send(msg);
        } catch (IOException e) {
            Log.d(TAG, "Message sending failure.", e);
        }
    }

    /**
     * Sends a message to the client.
     * Caller method wont be blocked.
     *
     * @param msg message to send
     * @see #sendBlocking
     */
    public void sendNonBlocking(Message msg) {
        sendExecutor.submit(() -> sendBlocking(msg));
    }

    /**
     * Creates a new thread to verify the client by taking a token.
     *
     * @param client a {@link JsonSocket} which is connected
     * @see NetServer#accept
     */
    @Override
    protected void accept(JsonSocket client) {
        executor.submit(() -> {
            boolean valid = false;
            try {
                valid = verifyClient(client);
            } catch (Exception e) {
                valid = false;
            }
            if (valid) {
                changeClient(client);
            } else {
                Log.i(TAG, "Client rejected.");
                try {
                    client.close();
                } catch (Exception ignored) {
                }
            }
        });
    }

    /**
     * Verifies the client by taking a token.
     *
     * @param client client
     * @throws Exception if verification is failed
     * @see #accept
     */
    private boolean verifyClient(JsonSocket client) throws Exception {
        Future<Message> futureMessage
                = executor.submit(() -> client.get(Message.class));
        Message token = futureMessage.get(1000, TimeUnit.SECONDS);
        return token != null && MessageTypes.TOKEN.equals(token.getType()) &&
                token.getInfo().has("token") &&
                mToken.equals(token.getInfo().get("token").getAsString());
    }

    /**
     * Changes current client to the specified client.
     * It actually closes the previous client (if exists) and then creates a
     * thread for the new one.
     *
     * @param client new client
     * @see #verifyClient
     */
    private void changeClient(JsonSocket client) {
        mClientLock.lock();
        try {
            // close previous socket
            if (mClient != null)
                mClient.close();
        } catch (Exception e) {
            Log.i(TAG, "Socket closing failure.", e);
        } finally {
            // change the client
            mClient = client;
            // notify waiting threads
            synchronized (clientNotifier) {
                clientNotifier.notifyAll();
            }
            mClientLock.unlock();
        }
    }

    @Override
    public synchronized void listen(int port) {
        Log.i(TAG, "Listening on port " + port + ".");
        executor = Executors.newCachedThreadPool();
        sendExecutor = Executors.newSingleThreadExecutor();
        super.listen(port);
    }

    @Override
    public synchronized void terminate() {
        super.terminate();
        if (executor != null)
            executor.shutdownNow();
        executor = null;
        if (sendExecutor != null)
            sendExecutor.shutdownNow();
        sendExecutor = null;
    }

    /**
     * Returns true if any client is connected and verified by the engine.
     *
     * @return true if there is any clients
     */
    public boolean hasClient() {
        return mClient != null;
    }

    /**
     * Caller will be blocked until a client is connected.
     * If currently a client is connected, returns without waiting.
     *
     * @throws InterruptedException if the current thread is interrupted.
     */
    public void waitForClient() throws InterruptedException {
        synchronized (clientNotifier) {
            if (hasClient())
                return;
            clientNotifier.wait();
        }
    }

    /**
     * Caller will be blocked until a client is connected or the
     * timeout is reached.
     * If currently a client is connected, returns without waiting.
     *
     * @param timeout timeout in seconds
     * @throws InterruptedException if the current thread is interrupted.
     */
    public void waitForClient(long timeout) throws InterruptedException {
        synchronized (clientNotifier) {
            if (hasClient())
                return;
            clientNotifier.wait(timeout);
        }
    }

    /**
     * Caller will be blocked until a <b>new</b> client is connected.
     *
     * @throws InterruptedException if the current thread is interrupted.
     */
    public void waitForNewClient() throws InterruptedException {
        synchronized (clientNotifier) {
            clientNotifier.wait();
        }
    }

    /**
     * Caller will be blocked until a <b>new</b> client is connected or the
     * timeout is reached.
     *
     * @param timeout timeout in seconds
     * @throws InterruptedException if the current thread is interrupted.
     */
    public void waitForNewClient(long timeout) throws InterruptedException {
        synchronized (clientNotifier) {
            clientNotifier.wait(timeout);
        }
    }

}