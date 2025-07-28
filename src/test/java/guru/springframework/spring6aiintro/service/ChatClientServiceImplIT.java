package guru.springframework.spring6aiintro.service;

import guru.springframework.spring6aiintro.dto.chat.ChatRequest;
import guru.springframework.spring6aiintro.dto.chat.ChatResponse;
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
import static org.hamcrest.Matchers.*;


@SpringBootTest
@ActiveProfiles("local")
@Slf4j
class ChatClientServiceImplIT {

    @Autowired
    ChatClientService chatClientService;

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
    void testProcessSimpleQuestion() {
        ChatRequest request = new ChatRequest("Was sind Ihre Öffnungszeiten?");
        ChatResponse response = chatClientService.processMessage(request);

        assertThat(response.response(), allOf(
            notNullValue(),
            containsString("Öffnungszeiten"),
            not(containsString("technical difficulty")),
            not(containsString("❌"))
        ));
        assertThat(response.response().length(), greaterThan(20));
    }

    @Test
    void testProcessTechnicalSupport() {
        ChatRequest request = new ChatRequest(
            "Meine Anwendung startet nicht. Beim Start erscheint die Fehlermeldung 'Port bereits in Verwendung'. Was kann ich tun?"
        );
        ChatResponse response = chatClientService.processMessage(request);

        assertThat(response.response(), allOf(
            notNullValue(),
            containsString("Port"),
            anyOf(
                containsString("können"),
                containsString("müssen"),
                containsString("sollten"),
                containsString("folgende Schritte"),
                containsString("Schritte, die")
            ),
            not(containsString("technical difficulty"))
        ));
        assertThat(response.response().length(), greaterThan(50));
    }

    @Test
    void testProcessComplexInquiry() {
        ChatRequest request = new ChatRequest(
            "Ich möchte meine Datenbank von MySQL auf PostgreSQL migrieren. " +
                "Welche Schritte sind notwendig und worauf muss ich besonders achten?"
        );
        ChatResponse response = chatClientService.processMessage(request);

        assertThat(response.response(), allOf(
            notNullValue(),
            containsString("MySQL"),
            containsString("PostgreSQL"),
            containsString("Migration"),
            not(containsString("technical difficulty"))
        ));
        assertThat(response.response().length(), greaterThan(100));
    }

    @Test
    void testProcessMultilingualSupport() {
        ChatRequest request = new ChatRequest(
            "How can I configure my application.properties for database connection?"
        );
        ChatResponse response = chatClientService.processMessage(request);

        assertThat(response.response(), allOf(
            notNullValue(),
            containsString("application.properties"),
            containsString("database"),
            not(containsString("technical difficulty"))
        ));
        assertThat(response.response().length(), greaterThan(50));
    }

    @Test
    void testQuickQuery() {
        ChatRequest request = new ChatRequest("2+2?");
        ChatResponse response = chatClientService.processSimpleQuery(request);

        assertThat(response.response(), allOf(
            notNullValue(),
            containsString("4")
        ));
        // Erwarte sehr kurze Antwort
        assertThat(response.response().length(), both(greaterThan(0)).and(lessThan(10)));
    }
}