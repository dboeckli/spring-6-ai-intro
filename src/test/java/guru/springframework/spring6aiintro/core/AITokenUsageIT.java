package guru.springframework.spring6aiintro.core;

import guru.springframework.spring6aiintro.test.config.OpenApiKeyExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
@ExtendWith(OpenApiKeyExtension.class)
@AutoConfigureTestRestTemplate
@Slf4j
class AITokenUsageIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${local.server.port}")
    private int port;

    @Test
    void testTokenUsageForQuickQuery() {
        double input = getTokenUsage("input");
        double output = getTokenUsage("output");
        double total = getTokenUsage("total");

        log.info("""
                📊 Token-Nutzung Details:
                Input Tokens: {}
                Output Tokens: {}
                Gesamt: {}""",
            input,
            output,
            total
        );

        double expectedInput = 13.0;
        double expectedMinOutput = 1.0;
        double expectedMaxOutput = 8.0;
        double expectedMinTotal = expectedInput + expectedMinOutput;
        double expectedMaxTotal = expectedInput + expectedMaxOutput;

        assertEquals(expectedInput, input);
        assertThat(output, allOf(greaterThanOrEqualTo(expectedMinOutput), lessThanOrEqualTo(expectedMaxOutput)));
        assertThat(total, allOf(greaterThanOrEqualTo(expectedMinTotal), lessThanOrEqualTo(expectedMaxTotal)));
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

    record MetricsResponse(List<Measurement> measurements) {
    }

    record Measurement(double value) {
    }

}
