package guru.springframework.spring6aiintro.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import guru.springframework.spring6aiintro.dto.Answer;
import guru.springframework.spring6aiintro.dto.GetCapitalDetailsResponse;
import guru.springframework.spring6aiintro.dto.GetCapitalRequest;
import guru.springframework.spring6aiintro.dto.GetCapitalResponse;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@ActiveProfiles("local")
@Slf4j
class OpenAIServiceImplIT {
    
    @Autowired
    OpenAIService openAIService;

    @BeforeAll
    static void setup() throws IOException {
        Path envFile = Paths.get(".run", ".openapi-key-env");
        if (Files.exists(envFile)) {
            List<String> lines = Files.readAllLines(envFile);
            for (String line : lines) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    System.setProperty(parts[0], parts[1]);
                }
            }
        } else {
            log.info("Warning: .openapi-key-env file not found. Ensure it exists or set OPENAI_API_KEY manually.");
        }
    }

    @Test
    void testGetAnswer() {
        String answer = openAIService.getAnswer("What is the capital of France?");
        assertThat(answer, containsString("Paris"));
    }

    @Test
    void testGetCapital() {
        GetCapitalRequest request = new GetCapitalRequest("France");
        Answer answer = openAIService.getCapital(request);
        assertThat(answer.answer(), containsString("Paris"));
    }

    @Test
    void testGetCapitalAsJson() {
        GetCapitalRequest request = new GetCapitalRequest("Germany");
        Answer answer = openAIService.getCapitalAsJson(request);
        assertThat(answer.answer(), containsString("Berlin"));
    }

    @Test
    void testGetCapitalUseParserForResponse() {
        GetCapitalRequest request = new GetCapitalRequest("Italy");
        GetCapitalResponse response = openAIService.getCapitalUseParserForResponse(request);
        assertEquals("Rome", response.answer());
    }

    @Test
    void testGetCapitalWithInfo() {
        GetCapitalRequest request = new GetCapitalRequest("Spain");
        Answer answer = openAIService.getCapitalWithInfo(request);
        assertThat(answer.answer(), containsString("Madrid"));
        assertThat(answer.answer().length(), greaterThan(20));
    }

    @Test
    void testGetCapitalWithInfoWithParser() {
        GetCapitalRequest request = new GetCapitalRequest("Portugal");
        GetCapitalDetailsResponse response = openAIService.getCapitalWithInfoWithParser(request);
        assertEquals("Lisbon", response.capital());
    }

    @Test
    void testGetRawResponse() throws JsonProcessingException {
        String input = "2+2=?";
        ChatResponse response = openAIService.getRawResponse(input);

        assertThat(response.getResult().getOutput().getText(), containsStringIgnoringCase("4"));
        assertThat(response.getMetadata().getModel(), notNullValue());
        assertThat(response.getMetadata().getUsage(), notNullValue());
        assertThat(response.getMetadata().getRateLimit(), notNullValue());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        Map<String, Object> jsonOutput = new LinkedHashMap<>();

        jsonOutput.put("model", response.getMetadata().getModel());
        jsonOutput.put("usage", response.getMetadata().getUsage());
        jsonOutput.put("rateLimit", response.getMetadata().getRateLimit());

        jsonOutput.put("promptMetadata", response.getMetadata().getPromptMetadata());

        jsonOutput.put("resultMetadata", response.getResult().getMetadata());
        jsonOutput.put("input", input);
        jsonOutput.put("result", response.getResult().getOutput().getText());

        String jsonString = objectMapper.writeValueAsString(jsonOutput);

        log.info("Response in JSON format:\n{}", jsonString);
    }

    @Test
    void testCheckAi() {
        String response = assertDoesNotThrow(() -> openAIService.checkAi());
        assertThat(response, allOf(
            Matchers.notNullValue(),
            not(emptyString())
        ));

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.isObject() && jsonNode.has("result")) {
                response = jsonNode.get("result").asText();
            }
        } catch (JsonProcessingException e) {
            log.info("Response is not a valid JSON. Treating it as a plain string.");
        }

        assertThat(response, allOf(
            containsString("2 + 2"),
            anyOf(
                containsString("= 4"),
                containsString("equals 4")
            )
        ));
        assertThat(response.length(), greaterThan(0));
    }

}
