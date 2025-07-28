package guru.springframework.spring6aiintro.service;

import guru.springframework.spring6aiintro.dto.chat.ChatRequest;
import guru.springframework.spring6aiintro.dto.chat.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChatClientServiceImpl implements ChatClientService {

    private final ChatClient chatClient;

    private final String SYSTEM_PROMPT = getSystemPrompt();

    public ChatClientServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public ChatResponse processMessage(ChatRequest chatRequest) {
        log.info("🎯 Processing customer support request");

        try {
            // Send the user's message to the AI model via OpenRouter
            String content = chatClient.prompt().system(SYSTEM_PROMPT).user(chatRequest.message())  // Set the user's message
                .call()                       // Make the API call
                .content();                   // Extract the response content

            log.info("✅ Support response generated with full observability");
            return new ChatResponse(content);

        } catch (Exception e) {
            // Log the error for debugging while providing user-friendly response
            log.error("❌ Error processing message: " + e.getMessage());
            return new ChatResponse("I apologize for the technical difficulty. Please try again in a moment.");
        }
    }

    public ChatResponse processSimpleQuery(ChatRequest chatRequest) {
        log.info("📝 Processing simple query");
        try {
            String content = chatClient.prompt()
                .system("Answer briefly in the same language as the question. No greetings or explanations.")
                .user(chatRequest.message())
                .call()
                .content();
            return new ChatResponse(content);
        } catch (Exception e) {
            log.error("❌ Error: " + e.getMessage());
            return new ChatResponse("Error occurred.");
        }
    }


    private String getSystemPrompt() {
        return """
        You are a helpful customer support assistant.
        
        Your responsibilities:
        - Answer product questions and provide specifications
        - Help troubleshoot technical issues step-by-step
        - Assist with account and billing inquiries
        - Provide clear, concise, and professional responses
        
        Important company information:
        Business hours:
        - Monday to Friday: 9:00 AM - 5:00 PM
        - Saturday and Sunday: Closed
        
        Support channels:
        - Email: support@company.com
        - Phone: +49 (0) 123 456789 during business hours
        
        Special instructions:
        - Always respond in the same language as the user's question
        - When asked about opening hours ("Öffnungszeiten"), always provide the complete schedule
        - Provide responses in a friendly and helpful tone
        
        If you cannot resolve an issue completely, guide the customer
        to contact human support with specific next steps.
        """;

    }
}
