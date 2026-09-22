package com.smartwms.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_message")
public class AiMessage {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String conversationId;

    private String role;

    private String content;

    private String toolCalls;

    private Integer tokens;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
