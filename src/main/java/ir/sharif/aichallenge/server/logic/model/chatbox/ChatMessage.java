package ir.sharif.aichallenge.server.logic.model.chatbox;

public class ChatMessage {
    private String message;
    private int value;
    private int messageLength;
    private int turn;

    public ChatMessage(String message, int value, int turn) {
        this.message = message;
        this.value = value;
        this.messageLength = messageLength;
        this.turn = turn;
    }

    public String getMessage() {
        return message;
    }

    public int getValue() {
        return value;
    }

    public int getMessageLength() {
        return messageLength;
    }

    public int getTurn() {
        return turn;
    }
}
