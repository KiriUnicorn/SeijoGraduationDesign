package com.competition.backend.entity;

import lombok.Data;

@Data
public class Admin {
    private Integer adminId;    // 管理员唯一ID
    private String name;        // 管理员姓名
    private String email;       // 管理员邮箱（唯一登录账号）
    private String password;    // 登录密码（bcrypt哈希存储）
} 