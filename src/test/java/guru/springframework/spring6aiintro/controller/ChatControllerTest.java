package guru.springframework.spring6aiintro.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import guru.springframework.spring6aiintro.dto.chat.ChatClientRequest;
import guru.springframework.spring6aiintro.dto.chat.ChatClientResponse;
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
    void checkAi_Success() throws JsonProcessingException {
        // Given
        String expectedResponse = "AI System ist betriebsbereit";
        when(chatService.checkAi()).thenReturn(expectedResponse);

        // When
        ResponseEntity<String> responseEntity = chatController.checkAi();

        // Then
        assertAll("AI Check Erfolgsfall",
            () -> assertEquals(200, responseEntity.getStatusCode().value(), "HTTP Status sollte 200 OK sein"),
            () -> assertEquals(expectedResponse, responseEntity.getBody(), "Response Body sollte übereinstimmen"),
            () -> verify(chatService).checkAi()
        );
    }

    @Test
    void checkAi_HandlesException() throws JsonProcessingException {
        // Given
        String errorMessage = "JSON Verarbeitungsfehler";
        when(chatService.checkAi()).thenThrow(new JsonProcessingException(errorMessage) {});

        // When
        ResponseEntity<String> responseEntity = chatController.checkAi();

        // Then
        assertAll("AI Check Fehlerfall",
            () -> assertEquals(500, responseEntity.getStatusCode().value(), "HTTP Status sollte 500 sein"),
            () -> assertEquals("Error processing AI check: " + errorMessage, responseEntity.getBody(),
                "Fehlermeldung sollte korrekt formatiert sein"),
            () -> verify(chatService).checkAi()
        );
    }



}