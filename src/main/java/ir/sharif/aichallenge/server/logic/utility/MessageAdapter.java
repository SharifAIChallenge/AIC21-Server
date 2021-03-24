package ir.sharif.aichallenge.server.logic.utility;

import ir.sharif.aichallenge.server.common.network.data.ClientMessageInfo;
import ir.sharif.aichallenge.server.common.network.data.SendMessageInfo;
import ir.sharif.aichallenge.server.logic.model.chatbox.ChatMessage;

import java.util.List;
import java.util.stream.Collectors;

public class MessageAdapter {
    public List<ChatMessage> convertToChatMessage(List<ClientMessageInfo> messages, int currentTurn) {
        return messages.stream().map(x -> (SendMessageInfo) (x))
                .map(x -> new ChatMessage(x.getMessage(), x.getValue(), currentTurn, x.getPlayerId()))
                .collect(Collectors.toList());
    }
}
