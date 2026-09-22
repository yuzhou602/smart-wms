package com.smartwms.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartwms.ai.entity.AiConversation;
import com.smartwms.ai.entity.AiMessage;
import com.smartwms.ai.mapper.AiConversationMapper;
import com.smartwms.ai.mapper.AiMessageMapper;
import com.smartwms.ai.service.AiService;
import com.smartwms.ai.tools.WarehouseTools;
import com.smartwms.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnBean(ChatModel.class)
public class AiServiceImpl implements AiService {

    private final AiConversationMapper conversationMapper;
    private final AiMessageMapper messageMapper;
    private final ChatModel chatModel;
    private final WarehouseTools warehouseTools;

    @Override
    @Transactional
    public AiConversation createConversation(Long userId, String title) {
        AiConversation conversation = new AiConversation();
        conversation.setConversationId(UUID.randomUUID().toString());
        conversation.setUserId(userId);
        conversation.setTitle(title != null ? title : "新对话");
        conversationMapper.insert(conversation);
        return conversation;
    }

    @Override
    @Transactional
    public AiMessage chat(String conversationId, String message, Long userId) {
        AiConversation conversation = requireOwnedConversation(conversationId, userId);
        AiMessage userMessage = new AiMessage();
        userMessage.setConversationId(conversationId);
        userMessage.setRole("user");
        userMessage.setContent(message);
        messageMapper.insert(userMessage);

        String systemPrompt = "你是仓储运营助手，基于真实数据分析回答问题。回答要包含：数据依据、分析、风险、建议。";

        try {
            ChatClient chatClient = ChatClient.builder(chatModel)
                    .defaultTools(warehouseTools)
                    .build();

            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(message)
                    .call()
                    .content();

            AiMessage assistantMessage = new AiMessage();
            assistantMessage.setConversationId(conversationId);
            assistantMessage.setRole("assistant");
            assistantMessage.setContent(response);
            messageMapper.insert(assistantMessage);

            conversation.setUpdatedAt(LocalDateTime.now());
            conversationMapper.updateById(conversation);

            return assistantMessage;
        } catch (Exception e) {
            AiMessage errorMessage = new AiMessage();
            errorMessage.setConversationId(conversationId);
            errorMessage.setRole("assistant");
            errorMessage.setContent("抱歉，处理您的请求时出现错误：" + e.getMessage());
            messageMapper.insert(errorMessage);
            return errorMessage;
        }
    }

    @Override
    public List<AiConversation> listConversations(Long userId) {
        LambdaQueryWrapper<AiConversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiConversation::getUserId, userId);
        wrapper.orderByDesc(AiConversation::getUpdatedAt);
        wrapper.last("LIMIT 50");
        return conversationMapper.selectList(wrapper);
    }

    @Override
    public List<AiMessage> getMessages(String conversationId, Long userId) {
        requireOwnedConversation(conversationId, userId);
        LambdaQueryWrapper<AiMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiMessage::getConversationId, conversationId);
        wrapper.orderByAsc(AiMessage::getCreatedAt);
        return messageMapper.selectList(wrapper);
    }

    private AiConversation requireOwnedConversation(String conversationId, Long userId) {
        AiConversation conversation = conversationMapper.selectOne(new LambdaQueryWrapper<AiConversation>()
                .eq(AiConversation::getConversationId, conversationId)
                .eq(AiConversation::getUserId, userId));
        if (conversation == null) throw new BusinessException("对话不存在或无权访问");
        return conversation;
    }
}
