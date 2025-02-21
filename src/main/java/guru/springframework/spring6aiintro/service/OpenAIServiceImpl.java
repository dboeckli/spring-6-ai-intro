package guru.springframework.spring6aiintro.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6aiintro.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Slf4j
public class OpenAIServiceImpl implements OpenAIService {
    
    private final ChatModel chatModel;

    private final ObjectMapper objectMapper;

    @Value("classpath:templates/get-capital-prompt.st")
    private Resource getCapitalPrompt;

    @Value("classpath:templates/get-capital-prompt-as-json.st")
    private Resource getCapitalPromptInJson;

    @Value("classpath:templates/get-capital-with-info.st")
    private Resource getCapitalPromptWithInfo;

    @Value("classpath:templates/get-capital-prompt-with-parser.st")
    private Resource getCapitalWithParser;

    @Value("classpath:templates/get-capital-with-info-with-parser.st")
    private Resource getCapitalDetailsWithParser;

    public OpenAIServiceImpl(ChatModel chatModel, ObjectMapper objectMapper) {
        this.chatModel = chatModel;
        this.objectMapper = objectMapper;
    }
    
    @Override
    public String getAnswer(String question) {
        PromptTemplate promptTemplate = new PromptTemplate(question);
        Prompt prompt = promptTemplate.create();
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

    @Override
    public Answer getAnswer(Question question) {
        PromptTemplate promptTemplate = new PromptTemplate(question.question());
        Prompt prompt = promptTemplate.create();
        ChatResponse response = chatModel.call(prompt);
        return new Answer(response.getResult().getOutput().getText());
    }

    @Override
    public Answer getCapital(GetCapitalRequest getCapitalRequest) {
        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPrompt);
        Prompt prompt = promptTemplate.create(Map.of("stateOrCountry", getCapitalRequest.countryName()));
        ChatResponse response = chatModel.call(prompt);

        return new Answer(response.getResult().getOutput().getText());
    }

    @Override
    public Answer getCapitalWithInfo(GetCapitalRequest getCapitalRequest) {
        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPromptWithInfo);
        Prompt prompt = promptTemplate.create(Map.of("stateOrCountry", getCapitalRequest.countryName()));
        ChatResponse response = chatModel.call(prompt);

        return new Answer(response.getResult().getOutput().getText());
    }

    @Override
    public GetCapitalDetailsResponse getCapitalWithInfoWithParser(GetCapitalRequest getCapitalRequest) {
        BeanOutputConverter<GetCapitalDetailsResponse> converter = new BeanOutputConverter<>(GetCapitalDetailsResponse.class);
        String format = converter.getFormat();

        log.info("Json Format: " + format);

        PromptTemplate promptTemplate = new PromptTemplate(getCapitalDetailsWithParser);
        Prompt prompt = promptTemplate.create(Map.of("stateOrCountry", getCapitalRequest.countryName(), "format", format));

        ChatResponse response = chatModel.call(prompt);
        return converter.convert(response.getResult().getOutput().getText());
    }

    @Override
    public Answer getCapitalAsJson(GetCapitalRequest getCapitalRequest) {
        PromptTemplate promptTemplate = new PromptTemplate(getCapitalPromptInJson);
        Prompt prompt = promptTemplate.create(Map.of("stateOrCountry", getCapitalRequest.countryName()));
        ChatResponse response = chatModel.call(prompt);

        log.info("Response: " + response.getResult().getOutput().getText());
        String responseString;
        try {
            JsonNode jsonNode = objectMapper.readTree(response.getResult().getOutput().getText());
            responseString = jsonNode.get("answer").asText();

        } catch (JsonProcessingException ex) {
            log.error("Error parsing JSON response: " + response.getResult().getOutput().getText(), ex);
            throw new RuntimeException(ex);
        }
        return new Answer(responseString);
    }


    @Override
    public GetCapitalResponse getCapitalUseParserForResponse(GetCapitalRequest getCapitalRequest) {
        BeanOutputConverter<GetCapitalResponse> converter = new BeanOutputConverter<>(GetCapitalResponse.class);
        String format = converter.getFormat();
        
        log.info("Json Format: " + format);
        
        PromptTemplate promptTemplate = new PromptTemplate(getCapitalWithParser);
        Prompt prompt = promptTemplate.create(Map.of("stateOrCountry", getCapitalRequest.countryName(), "format", format));

        ChatResponse response = chatModel.call(prompt);
        return converter.convert(response.getResult().getOutput().getText());
    }
}
