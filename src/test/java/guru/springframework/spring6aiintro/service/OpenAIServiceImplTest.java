package guru.springframework.spring6aiintro.service;

import guru.springframework.spring6aiintro.dto.Answer;
import guru.springframework.spring6aiintro.dto.GetCapitalDetailsResponse;
import guru.springframework.spring6aiintro.dto.GetCapitalRequest;
import guru.springframework.spring6aiintro.dto.GetCapitalResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest
@ActiveProfiles("local")
@Slf4j
class OpenAIServiceImplTest {
    
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
}
