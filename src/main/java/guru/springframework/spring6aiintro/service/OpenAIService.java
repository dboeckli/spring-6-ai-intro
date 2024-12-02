package guru.springframework.spring6aiintro.service;

import guru.springframework.spring6aiintro.dto.*;

public interface OpenAIService {

    String getAnswer(String question);

    Answer getAnswer(Question question);

    Answer getCapital(GetCapitalRequest getCapitalRequest);

    GetCapitalResponse getCapitalUseParserForResponse(GetCapitalRequest getCapitalRequest);

    Answer getCapitalAsJson(GetCapitalRequest getCapitalRequest);

    Answer getCapitalWithInfo(GetCapitalRequest getCapitalRequest);

    GetCapitalDetailsResponse getCapitalWithInfoWithParser(GetCapitalRequest getCapitalRequest);

}
