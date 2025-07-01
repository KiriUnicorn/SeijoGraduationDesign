package com.competition.backend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成管理员密码
        System.out.println("管理员密码(admin123): " + encoder.encode("admin123"));
        
        // 生成教师密码
        System.out.println("教师密码(teacher123): " + encoder.encode("teacher123"));
        
        // 生成学生密码
        System.out.println("学生密码(student123): " + encoder.encode("student123"));
    }
} 