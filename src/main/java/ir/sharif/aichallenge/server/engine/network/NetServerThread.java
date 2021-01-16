package ir.sharif.aichallenge.server.engine.network;

import ir.sharif.aichallenge.server.common.network.JsonSocket;
import ir.sharif.aichallenge.server.common.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.function.Consumer;

/**
 * This thread is used by {@link NetServer} to listen on
 * some port for the new clients.
 * <p>
 * The actual work of the {@link NetServerThread} is done by a
 * {@link ServerSocket}. When a <code>NetServerThread</code> is ran,
 * it creates a new engine socket to listen on that port. When a new client is
 * connected to that port, it passes the client's
 * {@link JsonSocket} to the specified consumer.
 * <p>
 * The consumer should accept the client and perform necessary operations.
 * <p>
 * In the {@link NetServer} the consumer is the abstract method
 * {@link NetServer#accept}.
 * <p>
 * When the engine socket is disconnected from the port, e.g. when it is closed
 * due to an unknown reason, a new engine socket will be created.
 * So it is guaranteed that engine is always listening on the port.
 *
 * @see NetServer
 * @see ServerSocket
 */
public class NetServerThread extends Thread {

    /**
     * Logging tag.
     */
    private static final String TAG = "NetServerThread";

    /**
     * The port to listen on.
     */
    private final int port;
    /**
     * Terminate flag!
     */
    private boolean terminateFlag;
    /**
     * The underlying engine socket which actually listens on the port.
     */
    private ServerSocket serverSocket;
    /**
     * This consumer specifies the behavior of the engine when a new client is
     * connected.
     * Actually, <code>clientAcceptor.accept(JsonSocket)</code> is called when
     * a client is connected to the port.
     */
    private final Consumer<JsonSocket> clientAcceptor;

    /**
     * Constructor.
     *
     * @param port           the engine port
     * @param clientAcceptor specifies accept behavior of the engine
     */
    public NetServerThread(int port, Consumer<JsonSocket> clientAcceptor) {
        this.port = port;
        this.clientAcceptor = clientAcceptor;
    }

    /**
     * Runs the engine until the <code>terminateFlag</code> is set to
     * <code>true</code>.
     *
     * @see #runServer
     * @see #terminate
     */
    @Override
    public void run() {
        while (!terminateFlag)
            try {
                runServer();
            } catch (SocketException ignore) {

            } catch (Exception e) {
                Log.i(TAG, "Server socket closed", e);
            }
    }

    /**
     * Runs the engine and handles new clients. The method can not be completed
     * without throwing an exception.
     *
     * @throws IOException if an I/O error occurs,
     *                     e.g. when the engine socket is closed.
     * @see #terminate
     */
    private void runServer() throws IOException {
        if (serverSocket != null && !serverSocket.isClosed())
            serverSocket.close();
        serverSocket = new ServerSocket(port);
        while (!terminateFlag)
            clientAcceptor.accept(new JsonSocket(serverSocket.accept()));
    }

    /**
     * Terminates operations of the current thread. Sets the terminate flag
     * to <code>true</code> and closes the engine socket.
     */
    public synchronized void terminate() {
        terminateFlag = true;
        try {
            serverSocket.close();
        } catch (Exception e) {
            Log.i(TAG, "Socket closing failure.", e);
        }
    }

}
