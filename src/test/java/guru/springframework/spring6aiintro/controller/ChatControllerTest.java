package guru.springframework.spring6aiintro.controller;

import guru.springframework.spring6aiintro.dto.chat.ChatClientRequest;
import guru.springframework.spring6aiintro.dto.chat.ChatClientResponse;
import guru.springframework.spring6aiintro.dto.check.Conversation;
import guru.springframework.spring6aiintro.service.ChatClientService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@Slf4j
class ChatControllerTest {

    @Mock
    private ChatClientService chatService;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setUp() throws Exception {
        try (var ignored = openMocks(this)) {
            log.info("Mock setup complete");
        }
    }

    @Test
    void testSendMessage() {
        // Given
        ChatClientRequest chatClientRequest = new ChatClientRequest("test message");
        ChatClientResponse expectedResponse = new ChatClientResponse("test response");
        when(chatService.processMessage(chatClientRequest)).thenReturn(expectedResponse);

        // When
        ResponseEntity<ChatClientResponse> responseEntity = chatController.sendMessage(chatClientRequest);

        assertAll("Chat Response Validation",
            () -> assertEquals(200, responseEntity.getStatusCode().value(), "HTTP Status sollte 200 OK sein"),
            () -> assertEquals(expectedResponse, responseEntity.getBody(), "Response Body sollte übereinstimmen"),
            () -> verify(chatService).processMessage(chatClientRequest)
        );
    }

    @Test
    void testSendQuickQuery() {
        ChatClientRequest chatClientRequest = new ChatClientRequest("1+1?");
        ChatClientResponse expectedResponse = new ChatClientResponse("2");
        when(chatService.processSimpleQuery(chatClientRequest)).thenReturn(expectedResponse);

        // When
        ResponseEntity<ChatClientResponse> responseEntity = chatController.sendQuickQuery(chatClientRequest);

        assertAll("Chat Response Validation",
            () -> assertEquals(200, responseEntity.getStatusCode().value()),
            () -> assertEquals(expectedResponse, responseEntity.getBody()),
            () -> verify(chatService).processSimpleQuery(chatClientRequest)
        );
    }

    @Test
    void checkAi_Success() {
        // Given
        String expectedResponse = "AI is working";
        AssistantMessage expectedAssistantMessage = new AssistantMessage(expectedResponse);
        ChatResponse exptectedChatResponse = new ChatResponse(List.of(new Generation(expectedAssistantMessage)));

        Conversation expectedConversation = new Conversation(null , exptectedChatResponse);
        when(chatService.checkAi()).thenReturn(expectedConversation);

        // When
        ResponseEntity<Conversation> responseEntity = chatController.checkAi();
        Assertions.assertNotNull(responseEntity.getBody());
        String responseText = responseEntity.getBody()
            .chatResponse()
            .getResult()
            .getOutput()
            .getText();

        // Then
        assertAll("AI Check Erfolgsfall",
            () -> assertEquals(200, responseEntity.getStatusCode().value()),
            () -> assertEquals(expectedResponse, responseText),
            () -> verify(chatService).checkAi()
        );
    }
}