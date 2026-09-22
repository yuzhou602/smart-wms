package com.smartwms.ai.controller;

import com.smartwms.ai.entity.AiConversation;
import com.smartwms.ai.entity.AiMessage;
import com.smartwms.ai.service.AiService;
import com.smartwms.common.exception.BusinessException;
import com.smartwms.common.response.R;
import com.smartwms.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "AI智能助手", description = "AI对话和工具调用")
@RestController
@org.springframework.security.access.prepost.PreAuthorize("hasAuthority('ai:assistant')")
@RequestMapping("/ai")
public class AiController {

    @Autowired(required = false)
    private AiService aiService;

    private void checkAiAvailable() {
        if (aiService == null) {
            throw new BusinessException("AI服务未配置，请设置AI_API_KEY环境变量");
        }
    }

    @Operation(summary = "创建新对话")
    @PostMapping("/conversations")
    public R<AiConversation> createConversation(@RequestBody(required = false) String title) {
        checkAiAvailable();
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(aiService.createConversation(userId, title));
    }

    @Operation(summary = "获取对话列表")
    @GetMapping("/conversations")
    public R<List<AiConversation>> listConversations() {
        checkAiAvailable();
        Long userId = SecurityUtils.getCurrentUserId();
        return R.ok(aiService.listConversations(userId));
    }

    @Operation(summary = "获取对话消息")
    @GetMapping("/conversations/{conversationId}/messages")
    public R<List<AiMessage>> getMessages(@PathVariable String conversationId) {
        checkAiAvailable();
        return R.ok(aiService.getMessages(conversationId, SecurityUtils.getCurrentUserId()));
    }

    @Operation(summary = "发送消息")
    @PostMapping("/chat")
    public R<AiMessage> chat(@RequestBody ChatRequest request) {
        checkAiAvailable();
        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new BusinessException("消息内容不能为空");
        }
        if (request.getConversationId() == null || request.getConversationId().isEmpty()) {
            AiConversation conversation = aiService.createConversation(
                SecurityUtils.getCurrentUserId(),
                truncateTitle(request.getMessage())
            );
            request.setConversationId(conversation.getConversationId());
        }
        return R.ok(aiService.chat(request.getConversationId(), request.getMessage(), SecurityUtils.getCurrentUserId()));
    }

    private String truncateTitle(String message) {
        if (message == null) return "新对话";
        return message.length() > 50 ? message.substring(0, 50) + "..." : message;
    }

    @Data
    public static class ChatRequest {
        private String conversationId;
        private String message;
    }
}
