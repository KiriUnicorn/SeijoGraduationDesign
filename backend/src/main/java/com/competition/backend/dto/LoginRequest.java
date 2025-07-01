package com.competition.backend.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;    // 邮箱（唯一登录凭证）
    private String password; // 密码
} 