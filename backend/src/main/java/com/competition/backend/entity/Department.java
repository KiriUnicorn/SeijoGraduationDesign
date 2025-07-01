package com.competition.backend.entity;

import lombok.Data;

@Data
public class Department {
    private Integer departmentId;  // 院系唯一ID
    private String name;           // 院系名称
    private String contact;        // 院系联系方式
} 