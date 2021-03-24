package ir.sharif.aichallenge.server.logic.model.chatbox;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChatBoxTest {
    private ChatBox chatBox;

    @BeforeEach
    void setUp() {
        chatBox = new ChatBox();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addMessage() {
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage("1", 1, 1, 1));
        chatMessages.add(new ChatMessage("1", 2, 1, 1));
        chatMessages.add(new ChatMessage("1", 3, 1, 1));
        chatMessages.add(new ChatMessage("1", 3, 1, 1));

        chatBox.addMessage(chatMessages);
        List<ChatMessage> expectedMessages = chatBox.getChatMessages();

        assertEquals(2, expectedMessages.size());
        assertEquals(3, expectedMessages.get(0).getValue());
        assertEquals(3, expectedMessages.get(1).getValue());
    }

    @Test
    void getChatMessages() {
        assertEquals(0, chatBox.getChatMessages().size());
    }
}