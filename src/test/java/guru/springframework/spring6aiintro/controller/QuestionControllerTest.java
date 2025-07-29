package guru.springframework.spring6aiintro.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import guru.springframework.spring6aiintro.dto.*;
import guru.springframework.spring6aiintro.service.OpenAIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class QuestionControllerTest {

    @Mock
    private OpenAIService openAIService;

    @InjectMocks
    private QuestionController questionController;

    @BeforeEach
    void setUp() {
        openMocks(this);
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
    void checkAi_Success() throws JsonProcessingException {
        String expectedResponse = "AI is working";
        when(openAIService.checkAi()).thenReturn(expectedResponse);

        ResponseEntity<String> response = questionController.checkAi();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(expectedResponse, response.getBody());
        verify(openAIService).checkAi();
    }

    @Test
    void checkAi_HandlesException() throws JsonProcessingException {
        String errorMessage = "Parse error";
        when(openAIService.checkAi()).thenThrow(new JsonProcessingException(errorMessage) {});

        ResponseEntity<String> response = questionController.checkAi();

        assertEquals(500, response.getStatusCode().value());
        assertEquals("Error processing AI check: " + errorMessage, response.getBody());
        verify(openAIService).checkAi();
    }

}
