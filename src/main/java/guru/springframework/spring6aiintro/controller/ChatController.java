package guru.springframework.spring6aiintro.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import guru.springframework.spring6aiintro.dto.chat.ChatClientRequest;
import guru.springframework.spring6aiintro.dto.chat.ChatClientResponse;
import guru.springframework.spring6aiintro.service.ChatClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClientService chatClientService;

    /**
     * Handle customer support chat requests
     * Each request triggers complete observability pipeline
     */
    @PostMapping("/message")
    public ResponseEntity<ChatClientResponse> sendMessage(@RequestBody ChatClientRequest chatClientRequest) {
        log.info("📞 Incoming customer support request");

        // Process the message through our service
        ChatClientResponse chatClientResponse = chatClientService.processMessage(chatClientRequest);

        log.info("📋 Support response delivered");
        return ResponseEntity.ok(chatClientResponse);
    }

    @PostMapping("/quick")
    public ResponseEntity<ChatClientResponse> sendQuickQuery(@RequestBody ChatClientRequest chatClientRequest) {
        log.info("⚡ Quick query received: {}", chatClientRequest.message());
        return ResponseEntity.ok(chatClientService.processSimpleQuery(chatClientRequest));
    }

    @GetMapping("/check-ai")
    public ResponseEntity<String> checkAi() {
        try {
            String result = chatClientService.checkAi();
            return ResponseEntity.ok(result);
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().body("Error processing AI check: " + e.getMessage());
        }
    }

}
