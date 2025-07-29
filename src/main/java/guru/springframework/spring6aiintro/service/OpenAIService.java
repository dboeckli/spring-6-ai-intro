package guru.springframework.spring6aiintro.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import guru.springframework.spring6aiintro.dto.*;
import org.springframework.ai.chat.model.ChatResponse;

public interface OpenAIService {

    String getAnswer(String question);

    Answer getAnswer(Question question);

    Answer getCapital(GetCapitalRequest getCapitalRequest);

    GetCapitalResponse getCapitalUseParserForResponse(GetCapitalRequest getCapitalRequest);

    Answer getCapitalAsJson(GetCapitalRequest getCapitalRequest);

    Answer getCapitalWithInfo(GetCapitalRequest getCapitalRequest);

    GetCapitalDetailsResponse getCapitalWithInfoWithParser(GetCapitalRequest getCapitalRequest);

    ChatResponse getRawResponse(String prompt);

    String checkAi() throws JsonProcessingException;

}
