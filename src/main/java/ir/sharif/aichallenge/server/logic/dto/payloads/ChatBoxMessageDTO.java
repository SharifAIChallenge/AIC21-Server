package ir.sharif.aichallenge.server.logic.dto.payloads;

import ir.sharif.aichallenge.server.logic.model.chatbox.ChatMessage;

public class ChatBoxMessageDTO {
    String text;
    int turn;

    public ChatBoxMessageDTO(ChatMessage message){
        this.text = message.getMessage();
        this.turn = message.getTurn();
    }
}
