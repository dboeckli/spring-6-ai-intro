package guru.springframework.spring6aiintro.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import guru.springframework.spring6aiintro.dto.*;
import guru.springframework.spring6aiintro.service.OpenAIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final OpenAIService openAIService;

    @PostMapping("/ask")
    public Answer askQuestion(@RequestBody Question question) {
        return openAIService.getAnswer(question);
    }

    @PostMapping("/capital")
    public Answer getCapital(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.openAIService.getCapital(getCapitalRequest);
    }

    @PostMapping("/capitalResponseInJson")
    public Answer getCapitalInJson(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.openAIService.getCapitalAsJson(getCapitalRequest);
    }

    @PostMapping("/capitalResponseInJsonWithParser")
    public GetCapitalResponse getCapitalInJsonWithParser(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.openAIService.getCapitalUseParserForResponse(getCapitalRequest);
    }

    @PostMapping("/capitalWithInfo")
    public Answer getCapitalWithInfo(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.openAIService.getCapitalWithInfo(getCapitalRequest);
    }

    @PostMapping("/capitalWithInfWithParser")
    public GetCapitalDetailsResponse getCapitalWithInfoWithParser(@RequestBody GetCapitalRequest getCapitalRequest) {
        return this.openAIService.getCapitalWithInfoWithParser(getCapitalRequest);
    }

    @GetMapping("/check-ai")
    public ResponseEntity<String> checkAi() {
        try {
            String result = openAIService.checkAi();
            return ResponseEntity.ok(result);
        } catch (JsonProcessingException e) {
            return ResponseEntity.internalServerError().body("Error processing AI check: " + e.getMessage());
        }
    }
    
    
}
