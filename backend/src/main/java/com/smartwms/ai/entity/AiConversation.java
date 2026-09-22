package com.smartwms.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_conversation")
public class AiConversation {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String conversationId;

    private Long userId;

    private String title;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
