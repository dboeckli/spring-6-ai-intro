package guru.springframework.spring6aiintro;

import guru.springframework.spring6aiintro.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventListenerStartupBean {

    private final OpenAIService openAIService;

    private final ObjectMapper objectMapper;

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("###################################################");
        log.info("Starting AI check at startup event: " + event.toString());
        log.info("Conversation:\n" + objectMapper.writeValueAsString(openAIService.checkAi()));
        log.info("AI check completed successfully.");
        log.info("###################################################");
    }

}
