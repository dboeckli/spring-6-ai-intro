package guru.springframework.spring6aiintro.service;

import guru.springframework.spring6aiintro.dto.chat.ChatRequest;
import guru.springframework.spring6aiintro.dto.chat.ChatResponse;

public interface ChatClientService {

    ChatResponse processMessage(ChatRequest chatRequest);

}
