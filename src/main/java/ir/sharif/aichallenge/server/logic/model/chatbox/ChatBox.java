package ir.sharif.aichallenge.server.logic.model.chatbox;

import ir.sharif.aichallenge.server.common.network.Json;
import ir.sharif.aichallenge.server.logic.config.ConstConfigs;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ChatBox {
    private List<ChatMessage> chatMessages;

    public ChatBox() {
        this.chatMessages = new ArrayList<>();
    }

    public void addMessage(List<ChatMessage> messages){
        List<ChatMessage> selectedMessages  = messages.stream()
                                    .filter(x -> x.getMessageLength() <= ConstConfigs.MAX_MESSAGE_LENGTH)
                                    .sorted(Comparator.comparingInt(ChatMessage::getValue).reversed())
                                    .limit(ConstConfigs.CHAT_LIMIT).collect(Collectors.toList());
        chatMessages.addAll(selectedMessages);
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }
}
