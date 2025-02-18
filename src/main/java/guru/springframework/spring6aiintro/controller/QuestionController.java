package guru.springframework.spring6aiintro.controller;

import guru.springframework.spring6aiintro.dto.*;
import guru.springframework.spring6aiintro.service.OpenAIService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
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
    
    
}
