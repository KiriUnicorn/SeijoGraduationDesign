package com.competition.backend.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;     // JWT令牌
    private UserInfo userInfo; // 用户信息

    @Data
    public static class UserInfo {
        private Integer id;           // 用户ID
        private String name;          // 用户姓名
        private String email;         // 邮箱
        private String role;          // 角色（admin/teacher/student）
        private Integer departmentId; // 院系ID（仅教师和学生有）
    }
} 