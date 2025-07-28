package guru.springframework.spring6aiintro.core;

import guru.springframework.spring6aiintro.dto.chat.ChatRequest;
import guru.springframework.spring6aiintro.dto.chat.ChatResponse;
import guru.springframework.spring6aiintro.service.ChatClientService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
@Slf4j
public class AITokenUsageIT {

    @Autowired
    private ChatClientService chatClientService;

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${local.server.port}")
    private int port;

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
    void testTokenUsageForQuickQuery() {
        // Token-Nutzung vor der Anfrage erfassen
        double totalBefore = getTokenUsage("total");

        // Einfache Anfrage ausführen
        ChatRequest request = new ChatRequest("1+1?");
        ChatResponse response = chatClientService.processSimpleQuery(request);

        // Token-Nutzung nach der Anfrage
        double totalAfter = getTokenUsage("total");
        double tokensUsed = totalAfter - totalBefore;

        assertEquals(0.0, totalBefore);
        assertThat(response.response(), containsString("2"));
        assertThat(tokensUsed, greaterThan(1.0));

        log.info("""
            📊 Token-Nutzung Details:
            Input Tokens: {}
            Output Tokens: {}
            Gesamt: {}""",
            getTokenUsage("input"),
            getTokenUsage("output"),
            getTokenUsage("total")
        );
    }


    private double getTokenUsage(String type) {
        String url = String.format("http://localhost:%d/actuator/metrics/gen_ai.client.token.usage?tag=gen_ai.token.type:%s",
            port, type);
        ResponseEntity<MetricsResponse> response = restTemplate.getForEntity(url, MetricsResponse.class);
        return Optional.ofNullable(response.getBody())
            .map(MetricsResponse::measurements)
            .filter(m -> !m.isEmpty())
            .map(List::getFirst)
            .map(Measurement::value)
            .map(Double::longValue)
            .orElse(0L);

    }

    record MetricsResponse(List<Measurement> measurements) {}
    record Measurement(double value) {}

}
