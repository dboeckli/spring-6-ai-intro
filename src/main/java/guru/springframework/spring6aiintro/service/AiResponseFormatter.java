package guru.springframework.spring6aiintro.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.ai.chat.model.ChatResponse;

import java.util.LinkedHashMap;
import java.util.Map;

public class AiResponseFormatter {
    public static String formatAiCheckResponse(ChatResponse chatResponse, String input) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, Object> jsonOutput = new LinkedHashMap<>();

        jsonOutput.put("model", chatResponse.getMetadata().getModel());
        jsonOutput.put("usage", chatResponse.getMetadata().getUsage());
        jsonOutput.put("rateLimit", chatResponse.getMetadata().getRateLimit());
        jsonOutput.put("promptMetadata", chatResponse.getMetadata().getPromptMetadata());
        jsonOutput.put("resultMetadata", chatResponse.getResult().getMetadata());
        jsonOutput.put("input", input);
        jsonOutput.put("result", chatResponse.getResult().getOutput().getText());

        return objectMapper.writeValueAsString(jsonOutput);
    }

}
