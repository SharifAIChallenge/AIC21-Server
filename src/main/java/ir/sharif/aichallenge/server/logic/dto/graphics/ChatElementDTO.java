package ir.sharif.aichallenge.server.logic.dto.graphics;

public class ChatElementDTO {
    public String text;
    public int value;
    public int sender_id;

    public ChatElementDTO(String text, int value, int sender_id) {
        this.sender_id = sender_id;
        this.text = text;
        this.value = value;
    }
}
