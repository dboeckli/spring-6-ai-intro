package guru.springframework.spring6aiintro;

import guru.springframework.spring6aiintro.service.OpenAIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class Spring6AiIntroApplication {

    public static void main(String[] args) {
        SpringApplication.run(Spring6AiIntroApplication.class, args);
    }

    @Bean
    public CommandLineRunner runAiQuery(OpenAIService openAIService) {
        return args -> {
            log.info("Starting AI check at startup...");
            String jsonString = openAIService.checkAi();
            log.info("AI Response at Startup:");
            log.info(jsonString);
            log.info("AI check completed successfully.");
        };
    }

}
