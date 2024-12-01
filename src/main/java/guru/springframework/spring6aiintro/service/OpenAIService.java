package guru.springframework.spring6aiintro.service;

import guru.springframework.spring6aiintro.dto.Answer;
import guru.springframework.spring6aiintro.dto.GetCapitalRequest;
import guru.springframework.spring6aiintro.dto.Question;

public interface OpenAIService {

    String getAnswer(String question);

    Answer getAnswer(Question question);

    Answer getCapital(GetCapitalRequest getCapitalRequest);

    Answer getCapitalWithInfo(GetCapitalRequest getCapitalRequest);

    Answer getCapitalAsJson(GetCapitalRequest getCapitalRequest);
}
