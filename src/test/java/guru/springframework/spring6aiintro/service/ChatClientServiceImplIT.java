package guru.springframework.spring6aiintro.service;

import guru.springframework.spring6aiintro.dto.chat.ChatClientRequest;
import guru.springframework.spring6aiintro.dto.chat.ChatClientResponse;
import guru.springframework.spring6aiintro.dto.check.Conversation;
import guru.springframework.spring6aiintro.test.config.OpenApiKeyExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@ActiveProfiles("local")
@ExtendWith(OpenApiKeyExtension.class)
@Slf4j
class ChatClientServiceImplIT {

    @Autowired
    ChatClientService chatClientService;

    @Test
    void testProcessSimpleQuestion() {
        ChatClientRequest request = new ChatClientRequest("Was sind Ihre Öffnungszeiten?");
        ChatClientResponse response = chatClientService.processMessage(request);

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
        ChatClientRequest request = new ChatClientRequest(
            "Meine Anwendung startet nicht. Beim Start erscheint die Fehlermeldung 'Port bereits in Verwendung'. Was kann ich tun?"
        );
        ChatClientResponse response = chatClientService.processMessage(request);

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
        ChatClientRequest request = new ChatClientRequest(
            "Ich möchte meine Datenbank von MySQL auf PostgreSQL migrieren. " +
                "Welche Schritte sind notwendig und worauf muss ich besonders achten?"
        );
        ChatClientResponse response = chatClientService.processMessage(request);

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
        ChatClientRequest request = new ChatClientRequest(
            "How can I configure my application.properties for database connection?"
        );
        ChatClientResponse response = chatClientService.processMessage(request);

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
        ChatClientRequest request = new ChatClientRequest("2+2?");
        ChatClientResponse response = chatClientService.processSimpleQuery(request);

        assertThat(response.response(), allOf(
            notNullValue(),
            containsString("4")
        ));
        // Erwarte sehr kurze Antwort
        assertThat(response.response().length(), both(greaterThan(0)).and(lessThan(10)));
    }

    @Test
    void testCheckAi() {
        Conversation conversation = assertDoesNotThrow(() -> chatClientService.checkAi());

        assertNotNull(conversation);

        assertThat(conversation.chatResponse().getResult().getOutput().getText(), allOf(
            containsString("4")
        ));
    }
}