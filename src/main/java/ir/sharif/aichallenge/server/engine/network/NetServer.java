package ir.sharif.aichallenge.server.engine.network;

import ir.sharif.aichallenge.server.common.network.JsonSocket;

/**
 * This class implements a network engine. A <code>NetServer</code> runs a
 * engine on the specified port and waits for the clients to be connected,
 * and then based on the implementation of the <code>accept</code> method,
 * performs some operation for each client,
 * e.g. performs some operation based on the request,
 * returns some information to the requester,
 * or offers a terminal for the client.
 * <p>
 * When the method {@link #listen} is called, a
 * {@link NetServerThread} is assigned to listen on the specified
 * port. When a client attempts to connect to that port, the {@link #accept}
 * method of the class is called, with a reference to the
 * <code>JsonSocket</code> which is connected to the client. This is the initial
 * point of the connection and one can send initial messages to start
 * communication with the client.
 * <p>
 * It is better to do all operations for a client in a separated thread. So if
 * the client is fake it doesn't affect the behavior of the engine.
 *
 * @see NetServerThread
 * @see ClientNetwork
 * @see UINetwork
 */
public abstract class NetServer {

    /**
     * The thread which is assigned to listen on the port.
     */
    private NetServerThread listener;

    /**
     * A thread is created and assigned to listen on the specified port.
     * {@link #terminate} must be called between two consecutive calls of this
     * function.
     *
     * @param port engine port
     * @see #terminate
     * @see NetServerThread
     */
    public synchronized void listen(int port) {
        if (listener != null)
            throw new IllegalStateException("NetServer is currently listening.");
        listener = new NetServerThread(port, this::accept);
        listener.start();
    }

    /**
     * Terminates operations of the current listener. {@link #listen} must be
     * called between two consecutive calls of this function.
     *
     * @see #listen
     * @see NetServerThread
     */
    public synchronized void terminate() {
        if (listener == null)
            return;
        listener.terminate();
        listener = null;
    }

    /**
     * Returns the state of the engine.
     *
     * @return true if engine is currently listening.
     */
    public boolean isListening() {
        return listener != null;
    }

    /**
     * A method to accept new clients. When a client connects to the port of
     * this <code>NetServer</code>, this method is called, with a reference to
     * a {@link JsonSocket} which is connected to the client side
     * socket. A new thread may be assigned to handle requests of the client.
     * <p>
     * This method is abstract and so it is not implemented in this class.
     * Each engine has a different behavior on accepting new clients, e.g.
     * it may take a token from client or send some information at initial point.
     * To implement a engine, one can extend this class and implement this method.
     * <p>
     * Note that this method must take care of everything that could be happen
     * during communications between client and engine.
     *
     * @param client a {@link JsonSocket} which is connected
     *               to the client's socket.
     */
    protected abstract void accept(JsonSocket client);


}
