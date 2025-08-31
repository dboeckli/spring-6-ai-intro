package guru.springframework.spring6aiintro.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6aiintro.test.config.OpenApiKeyExtension;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@ExtendWith(OpenApiKeyExtension.class)
@Slf4j
class ActuatorInfoIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    BuildProperties buildProperties;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void actuatorInfoTest() throws Exception {
        mockMvc.perform(get("/actuator/info"))
            .andExpect(status().isOk())
            .andDo(result -> log.info("Response (pretty):\n{}", pretty(result.getResponse().getContentAsString())))

            .andExpect(jsonPath("$.git.commit.id.abbrev").isString())

            .andExpect(jsonPath("$.build.artifact").value(buildProperties.getArtifact()))
            .andExpect(jsonPath("$.build.group").value(buildProperties.getGroup()))

            .andExpect(jsonPath("$.java.version").value(startsWith("21")));
    }

    @Test
    void actuatorHealthTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/actuator/health/readiness"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andReturn();

        log.info("Response: {}", result.getResponse().getContentAsString());
    }

    private String pretty(String body) {
        try {
            Object json = OBJECT_MAPPER.readValue(body, Object.class);
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        } catch (Exception e) {
            // Falls kein valides JSON: unverändert zurückgeben
            return body;
        }
    }
    
}
