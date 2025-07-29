package guru.springframework.spring6aiintro.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import guru.springframework.spring6aiintro.dto.chat.ChatClientRequest;
import guru.springframework.spring6aiintro.dto.chat.ChatClientResponse;

public interface ChatClientService {

    ChatClientResponse processMessage(ChatClientRequest chatClientRequest);

    ChatClientResponse processSimpleQuery(ChatClientRequest chatClientRequest);

    String checkAi() throws JsonProcessingException;
}
