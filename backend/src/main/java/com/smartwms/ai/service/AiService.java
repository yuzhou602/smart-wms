package com.smartwms.ai.service;

import com.smartwms.ai.entity.AiConversation;
import com.smartwms.ai.entity.AiMessage;

import java.util.List;

public interface AiService {

    AiConversation createConversation(Long userId, String title);

    AiMessage chat(String conversationId, String message, Long userId);

    List<AiConversation> listConversations(Long userId);

    List<AiMessage> getMessages(String conversationId, Long userId);
}
