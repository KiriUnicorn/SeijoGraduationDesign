package com.competition.backend.entity;

import lombok.Data;

/**
 * 学生实体类
 */
@Data
public class Student {
    /**
     * 学生ID
     */
    private Integer studentId;

    /**
     * 所属院系ID
     */
    private Integer departmentId;

    /**
     * 学生姓名
     */
    private String name;



    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;
} 