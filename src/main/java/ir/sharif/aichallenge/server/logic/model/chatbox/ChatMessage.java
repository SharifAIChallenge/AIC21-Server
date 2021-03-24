package ir.sharif.aichallenge.server.logic.model.chatbox;

public class ChatMessage {
    private String message;
    private int value;
    private int messageLength;
    private int turn;
    private int sender_id;

    public ChatMessage(String message, int value, int turn, int sender_id) {
        this.message = message;
        this.value = value;
        this.messageLength = message.length();
        this.turn = turn;
        this.sender_id = sender_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
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
