package com.smartwms.system.user.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserVO {

    private Long id;
    private String username;
    private String realName;
    private String phone;
    private String email;
    private String avatar;
    private Integer status;
    private LocalDateTime createdAt;
    private List<String> permissions;
}
