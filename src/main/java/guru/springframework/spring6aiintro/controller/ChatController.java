package guru.springframework.spring6aiintro.controller;

import guru.springframework.spring6aiintro.dto.chat.ChatRequest;
import guru.springframework.spring6aiintro.dto.chat.ChatResponse;
import guru.springframework.spring6aiintro.service.ChatClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest chatRequest) {
        log.info("📞 Incoming customer support request");

        // Process the message through our service
        ChatResponse chatResponse = chatClientService.processMessage(chatRequest);

        log.info("📋 Support response delivered");
        return ResponseEntity.ok(chatResponse);
    }

    @PostMapping("/quick")
    public ResponseEntity<ChatResponse> sendQuickQuery(@RequestBody ChatRequest chatRequest) {
        log.debug("⚡ Quick query received: {}", chatRequest.message());
        return ResponseEntity.ok(chatClientService.processSimpleQuery(chatRequest));
    }

}
