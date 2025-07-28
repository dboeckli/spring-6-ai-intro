package guru.springframework.spring6aiintro.controller;

import guru.springframework.spring6aiintro.dto.chat.ChatRequest;
import guru.springframework.spring6aiintro.dto.chat.ChatResponse;
import guru.springframework.spring6aiintro.service.ChatClientService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

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
        ChatRequest chatRequest = new ChatRequest("test message");
        ChatResponse expectedResponse = new ChatResponse("test response");
        when(chatService.processMessage(chatRequest)).thenReturn(expectedResponse);

        // When
        ResponseEntity<ChatResponse> responseEntity = chatController.sendMessage(chatRequest);

        assertAll("Chat Response Validation",
            () -> assertEquals(200, responseEntity.getStatusCode().value(), "HTTP Status sollte 200 OK sein"),
            () -> assertEquals(expectedResponse, responseEntity.getBody(), "Response Body sollte übereinstimmen"),
            () -> verify(chatService).processMessage(chatRequest)
        );
    }

    @Test
    void testSendQuickQuery() {
        ChatRequest chatRequest = new ChatRequest("1+1?");
        ChatResponse expectedResponse = new ChatResponse("2");
        when(chatService.processSimpleQuery(chatRequest)).thenReturn(expectedResponse);

        // When
        ResponseEntity<ChatResponse> responseEntity = chatController.sendQuickQuery(chatRequest);

        assertAll("Chat Response Validation",
            () -> assertEquals(200, responseEntity.getStatusCode().value()),
            () -> assertEquals(expectedResponse, responseEntity.getBody()),
            () -> verify(chatService).processSimpleQuery(chatRequest)
        );
    }


}