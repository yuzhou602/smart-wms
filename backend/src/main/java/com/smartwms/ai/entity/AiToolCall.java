package com.smartwms.ai.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("ai_tool_call")
public class AiToolCall {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long messageId;

    private String toolName;

    private String arguments;

    private String result;

    private Long duration;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
