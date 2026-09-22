package com.smartwms.system.user.vo;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {

    private String token;
    private Long userId;
    private String username;
    private String realName;
    private List<String> permissions;
}
