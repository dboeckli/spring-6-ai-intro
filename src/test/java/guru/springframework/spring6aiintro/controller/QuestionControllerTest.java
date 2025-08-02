package guru.springframework.spring6aiintro.controller;

import guru.springframework.spring6aiintro.dto.*;
import guru.springframework.spring6aiintro.dto.check.Conversation;
import guru.springframework.spring6aiintro.service.OpenAIService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

@Slf4j
class QuestionControllerTest {

    @Mock
    private OpenAIService openAIService;

    @InjectMocks
    private QuestionController questionController;

    @BeforeEach
    void setUp() throws Exception {
        try (var ignored = openMocks(this)) {
            log.info("Mock setup complete");
        }
    }

    @Test
    void askQuestion() {
        Question question = new Question("What is the capital of France?");
        Answer expectedAnswer = new Answer("The capital of France is Paris.");
        when(openAIService.getAnswer(question)).thenReturn(expectedAnswer);

        Answer actualAnswer = questionController.askQuestion(question);

        assertEquals(expectedAnswer, actualAnswer);
        verify(openAIService).getAnswer(question);
    }

    @Test
    void getCapital() {
        GetCapitalRequest request = new GetCapitalRequest("France");
        Answer expectedAnswer = new Answer("Paris");
        when(openAIService.getCapital(request)).thenReturn(expectedAnswer);

        Answer actualAnswer = questionController.getCapital(request);

        assertEquals(expectedAnswer, actualAnswer);
        verify(openAIService).getCapital(request);
    }

    @Test
    void getCapitalInJson() {
        GetCapitalRequest request = new GetCapitalRequest("Germany");
        Answer expectedAnswer = new Answer("{\"capital\": \"Berlin\"}");
        when(openAIService.getCapitalAsJson(request)).thenReturn(expectedAnswer);

        Answer actualAnswer = questionController.getCapitalInJson(request);

        assertEquals(expectedAnswer, actualAnswer);
        verify(openAIService).getCapitalAsJson(request);
    }

    @Test
    void getCapitalInJsonWithParser() {
        GetCapitalRequest request = new GetCapitalRequest("Italy");
        GetCapitalResponse expectedResponse = new GetCapitalResponse("Rome");
        when(openAIService.getCapitalUseParserForResponse(request)).thenReturn(expectedResponse);

        GetCapitalResponse actualResponse = questionController.getCapitalInJsonWithParser(request);

        assertEquals(expectedResponse, actualResponse);
        verify(openAIService).getCapitalUseParserForResponse(request);
    }

    @Test
    void getCapitalWithInfo() {
        GetCapitalRequest request = new GetCapitalRequest("Spain");
        Answer expectedAnswer = new Answer("Madrid is the capital of Spain. It is located in the center of the country.");
        when(openAIService.getCapitalWithInfo(request)).thenReturn(expectedAnswer);

        Answer actualAnswer = questionController.getCapitalWithInfo(request);

        assertEquals(expectedAnswer, actualAnswer);
        verify(openAIService).getCapitalWithInfo(request);
    }

    @Test
    void getCapitalWithInfoWithParser() {
        GetCapitalRequest request = new GetCapitalRequest("Portugal");
        GetCapitalDetailsResponse expectedResponse = new GetCapitalDetailsResponse(
            "Lisbon", 
            500L,
            "Portugal", 
            "portugish",
            "Lisbon is the capital and largest city of Portugal.");
        when(openAIService.getCapitalWithInfoWithParser(request)).thenReturn(expectedResponse);

        GetCapitalDetailsResponse actualResponse = questionController.getCapitalWithInfoWithParser(request);

        assertEquals(expectedResponse, actualResponse);
        verify(openAIService).getCapitalWithInfoWithParser(request);
    }

    @Test
    void checkAi_Success() {
        String expectedResponse = "AI is working";
        AssistantMessage expectedAssistantMessage = new AssistantMessage(expectedResponse);
        ChatResponse exptectedChatResponse = new ChatResponse(List.of(new Generation(expectedAssistantMessage)));

        Conversation expectedConversation = new Conversation(null , exptectedChatResponse);
        when(openAIService.checkAi()).thenReturn(expectedConversation);

        ResponseEntity<Conversation> responseEntity = questionController.checkAi();
        Assertions.assertNotNull(responseEntity.getBody());
        String responseText = responseEntity.getBody()
            .chatResponse()
            .getResult()
            .getOutput()
            .getText();

        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(expectedResponse, responseText);
        verify(openAIService).checkAi();
    }
}
