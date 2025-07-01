package com.competition.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeamMember {
    private Integer teamId;        // 队伍ID
    private Integer studentId;     // 学生成员ID
    private String studentName;    // 学生姓名(通过JOIN查询获得)
    private LocalDateTime joinedAt; // 加入时间
} 