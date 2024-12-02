package guru.springframework.spring6aiintro.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;


@SpringBootTest
class OpenAIServiceImplTest {
    
    @Autowired
    OpenAIService openAIService;

    @Test
    void testGetAnswer() {
        String answer = openAIService.getAnswer("What is the capital of France?");
        assertThat(answer, containsString("Paris"));
    }
}
