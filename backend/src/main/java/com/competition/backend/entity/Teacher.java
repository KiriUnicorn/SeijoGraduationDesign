package com.competition.backend.entity;

import lombok.Data;

@Data
public class Teacher {
    private Integer teacherId;    // 教师唯一ID
    private String name;          // 教师姓名
    private Integer departmentId; // 所属院系ID
    private String departmentName;//所属院系名
    private String email;         // 教师邮箱
    private String password;      // 登录密码
} 