package guru.springframework.spring6aiintro.service;

import guru.springframework.spring6aiintro.dto.chat.ChatClientRequest;
import guru.springframework.spring6aiintro.dto.chat.ChatClientResponse;
import guru.springframework.spring6aiintro.dto.check.Conversation;

public interface ChatClientService {

    ChatClientResponse processMessage(ChatClientRequest chatClientRequest);

    ChatClientResponse processSimpleQuery(ChatClientRequest chatClientRequest);

    Conversation checkAi();
}
