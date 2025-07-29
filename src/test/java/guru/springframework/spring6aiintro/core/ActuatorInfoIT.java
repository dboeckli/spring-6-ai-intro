package guru.springframework.spring6aiintro.core;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Slf4j
class ActuatorInfoIT {

    @Autowired
    private MockMvc mockMvc;

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
    void actuatorInfoTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/actuator/info"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.git.commit.id").isString())
            
            .andExpect(jsonPath("$.build.javaVersion").value("21"))
            .andExpect(jsonPath("$.build.commit-id").isString())
            .andExpect(jsonPath("$.build.javaVendor").isString())
            .andExpect(jsonPath("$.build.artifact").value("spring-6-ai-intro"))
            .andExpect(jsonPath("$.build.group").value("guru.springframework"))
            .andReturn();
        
        log.info("Response: {}", result.getResponse().getContentAsString());
    }

    @Test
    void actuatorHealthTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/actuator/health/readiness"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andReturn();

        log.info("Response: {}", result.getResponse().getContentAsString());
    }
    
}
