package ir.sharif.aichallenge.server.engine.core;

import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.common.network.data.Message;
import ir.sharif.aichallenge.server.common.util.Log;
import ir.sharif.aichallenge.server.engine.config.Configs;
import ir.sharif.aichallenge.server.engine.network.UINetwork;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Class created as a part of {@link GameServer GameServer} class for controlling the output.
 * <p>
 * This class gathers all output taken from "GameLogic" into a queue, and the process them as the user
 * wishes. <i>Currently passing to {@link UINetwork UINetwork} and saving in a
 * local file is supported.</i>
 * </p>
 */
public class OutputController {

    public static final String TAG = "OutputController";

    private boolean sendToFile = false;
    private FileWriter fileWriter;
    private File outputFile;

    private boolean sendToUI = false;
    private UINetworkSender uiSender;
    private UINetwork uiNetwork;


    /**
     * Constructor with the properties about how outputs must be handled.
     * <p>
     * This constructor creates an instance of OutputController class with the given parameters.
     * The two booleans indicate that how the class will handle the given outputs.
     * In the case that sendToUI flag is set:
     * <p>
     * Output handling will be assumed as a "Pre Runner" in order to a fast forward run based
     * on the game logic, and storing data on a file for later use.
     * Data would be held on memory until reaches the specified buffer size, and then will be
     * written to the specified file (Using {@link FileWriter FileWriter}
     * runnable class).
     * </p>
     * In the case that sendToFile flag is set:
     * <p>
     * This type of output processing, will store outputs in the object, and sends it in regular
     * timer ticks. This time is specified in milliseconds as timeInterval
     * </p>
     * If the sendToUI or sendToFile flags were set, then the two other values would be assigned to the
     * appropriate instance values.
     * Note that this will raise a runtime exception, in the case of invalid arguments.
     * </p>
     *
     * @param uiNetwork The given instance of {@link UINetwork UINetwork} class to send data to
     */
    public OutputController(UINetwork uiNetwork) {
        this.sendToUI = Configs.PARAM_OC_SEND_TO_UI.getValue();
        if (sendToUI) {
            this.uiNetwork = uiNetwork;
            if (uiNetwork == null) {
                this.sendToUI = false;
                Log.i(TAG, "UINetwork parameter is null.");
            } else {
                uiSender = new UINetworkSender();
                new Thread(uiSender).start();
            }
//            timer = new Timer();
//            timer.scheduleAtFixedRate(new UINetworkSender(), 0, timeInterval);
        }
        this.sendToFile = Configs.PARAM_OC_SEND_TO_FILE.getValue();
        if (sendToFile) {
            this.outputFile = new File(Configs.PARAM_OC_FILE_PATH.getValue());
            try {
                fileWriter = new FileWriter(outputFile);
                new Thread(fileWriter).start();
            } catch (IOException e) {
                Log.i(TAG, "File writer could not be created.");
                sendToFile = false;
            }
        }
    }

    /**
     * Accepts an instance of {@link Message Message} class as an argument, and places it
     * on the queue.
     * <p>
     * In this method, the given message will be putted in the message queue, only if there's a place on the
     * queue. Otherwise the cleaning and caching processes will be done (through the
     * handleOverflow method).
     * Also if the buffer size condition is met, then the file writer method will be called with an
     * alternative thread, to save the contents on the file.
     * </p>
     *
     * @param message The given message (as output) to put in the message queue.
     */
    public synchronized void putMessage(Message message) {
        if (uiSender != null) {
            uiSender.queue(message);
        }
        if (fileWriter != null) {
            fileWriter.queue(message);
        }
    }
//
//    /**
//     * Method created to handle the possible overflows occurrence in the message queue.
//     * <p>
//     *      INCOMPLETE - Must be implemented to cache the queue to file.
//     * </p>
//     * @return True if the queue unblocked, false if any error occur during this operation
//     */
//    private boolean handleOverflow() {
//        messagesQueue.clear();
//        return true;
//    }

    /**
     * Tries to shutdown all the threads ran in this class so far.
     * <p>
     * This method calls the close on the {@link FileWriter FileWriter} instance of
     * the object, causing it to interrupt as soon as all files wrote down on the file.
     * Also cancels the timer to stop automatically invocation of
     * {@link UINetworkSender UINetworkSender}.
     * Other threads would be closed automatically ({@link UINetworkSender
     * UINetworkSenders} after the timeout).
     * </p>
     */
    public void shutdown() {
        if (fileWriter != null)
            fileWriter.close();
        fileWriter = null;
        if (uiSender != null)
            uiSender.close();
        uiSender = null;
    }

    public void waitToSend() throws InterruptedException {
        if (uiSender != null) {
            uiSender.waitToFinish();
        }
        if (fileWriter != null) {
            fileWriter.waitToFinish();
        }
    }

    /**
     * This inner class is used to do processes needed during the file saving operations, as an alternative
     * thread.
     * <p>
     * This class will be run by the "OutputController" class, as a file save operation is needed.
     * This Runnable implemented class, could do the long term file saving operations, as an alternative
     * thread.
     * This class uses a {@link java.util.concurrent.BlockingQueue BlockingQueue<LinkedList<Message>>}
     * implementation in order to save files without any block on the way of main thread.
     * </p>
     */
    private class FileWriter implements Runnable {

        private boolean open;
        private FileOutputStream outputStream;
        private final LinkedList<Message> messagesQueue;

        /**
         * Constructor of the class which accepts a File and sets it as the output of writing operations.
         *
         * @param file Given File to store message data in
         */
        public FileWriter(File file) throws IOException {
            outputStream = new FileOutputStream(file, false);
            messagesQueue = new LinkedList<>();
//            this.messagesQueue = new ArrayBlockingQueue<>(QUEUE_DEFAULT_SIZE);
        }

        public void waitToFinish() throws InterruptedException {
            synchronized (messagesQueue) {
                while (!messagesQueue.isEmpty())
                    messagesQueue.wait();
            }
        }

        public void queue(Message msg) {
            synchronized (messagesQueue) {
                messagesQueue.add(msg);
                messagesQueue.notifyAll();
            }
        }

        /**
         * The implemented run method of the {@link Runnable java.lang.Runnable} class which will
         * starts the thread listening for any input in order to save to file.
         * <p>
         * This method uses a while loop to save the whole contents of
         * {@link java.util.concurrent.BlockingQueue BlockingQueue} of
         * {@link Message Messages} to the given file.
         * This will stop working only if the close order were sent and there's no more
         * {@link Message Messages} on the queue.
         * </p>
         */
        @Override
        public void run() {
            if (open)
                return;
            open = true;
            try {
                while (open) {
                    synchronized (messagesQueue) {
                        while (!messagesQueue.isEmpty()) {
                            writeToFile(messagesQueue.pollFirst());
                            messagesQueue.notifyAll();
                        }
                        messagesQueue.wait();
                    }
                }
            } catch (InterruptedException ignored) {
            } finally {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
        }

        /**
         * Stores the contents of the message queue as an appendix to the FileWriter file.
         * <p>
         * This method will write the whole contents of the saved message queue in the file writer object
         * on the given file object by object. As this operation is completed, the file writer tries to write the
         * next file in the {@link java.util.concurrent.BlockingQueue BlockingQueue}
         * </p>
         */
        private void writeToFile(Message msg) {
            if (msg == null)
                return;
            try {
                System.err.println("writing " + Json.GSON.toJson(msg));
                outputStream.write(Json.GSON.toJson(msg).getBytes("UTF-8"));
                outputStream.write(0);
            } catch (IOException e) {
                Log.i(TAG, "Write to file failed.", e);
            }
        }

        /**
         * This will send to the class a request to close connection to the file.
         * <p>
         * This method sets the open flag of class to <i>false</i> and so as soon as
         * {@link java.util.concurrent.BlockingQueue BlockingQueue} is empty, the writing operation will be
         * finished and the thread will be killed.
         * </p>
         */
        public void close() {
            open = false;
        }
    }

    /**
     * Is responsible for sending the first {@link Message Message} in the queue to the
     * {@link UINetwork UINetwork}.
     * <p>
     * As the failure in connection and some other issues leaves the
     * {@link UINetwork#sendBlocking(Message) UINetwork.send(Message)} method
     * blocked, and so causes a thread block, this class runs as an alternative thread to send the messages
     * in the queue, to the {@link UINetwork UINetwrok} instance without causing the main
     * thread of OutputController to sleep.
     * </p>
     */
    private class UINetworkSender implements Runnable {

        private boolean open;
        private final LinkedList<Message> messagesQueue;

        public UINetworkSender() {
            messagesQueue = new LinkedList<>();
        }

        public void waitToFinish() throws InterruptedException {
            synchronized (messagesQueue) {
                while (!messagesQueue.isEmpty())
                    messagesQueue.wait();
            }
        }

        public void queue(Message msg) {
            if (msg == null) // prevent NullPointerException
                return;
            synchronized (messagesQueue) {
                messagesQueue.add(msg);
                messagesQueue.notifyAll();
            }
        }

        /**
         * Implemented run method from {@link Runnable Runnable} class which sends the first message in the
         * queue to the {@link UINetwork UINetwork}.
         * <p>
         * This method will call the {@link #sendToUINetwork(Message) sendToUINetwork(Message)}
         * method with the appropriate message instance.
         * Every connection made in a separate thread and with a specified timeout.
         * If the last connection could send the message within the timeout, then this method will take another
         * message from the queue and send to the network, otherwise, the last message will be sent again.
         * The caller timer will be canceled if the shutdown request was sent to the class.
         * </p>
         */
        @Override
        public void run() {
            if (open)
                return;
            open = true;
            try {
                while (open) {
                    synchronized (messagesQueue) {
                        while (!messagesQueue.isEmpty()) {
                            sendToUINetwork(messagesQueue.pollFirst());
                            messagesQueue.notifyAll();
                        }
                        messagesQueue.wait();
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }

        /**
         * This method serves the instance of {@link UINetwork UINetwork} class with messages.
         * <p>
         * This method calls {@link UINetwork#sendBlocking(Message) send(message)}
         * method on {@link UINetwork UINetwork} instance in order to show up on UI.
         * Calling this while the message queue is empty, will put it in the wait mode.
         * Basically this method will be called by the timer scheduled according to user preferred
         * time interval.
         * As the time of sending message to the {@link UINetwork UINetworks} exceeds the
         * timeout limit, then the execution will be cancelled.
         * </p>
         */
        private void sendToUINetwork(Message message) {
            if (message == null)
                return;
            uiNetwork.sendBlocking(message);
        }

        public void close() {
            open = false;
        }

    }
}
