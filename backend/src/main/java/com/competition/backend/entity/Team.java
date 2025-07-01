package com.competition.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Team {
    private Integer teamId;        // 队伍唯一ID
    private String name;           // 队伍名称
    private Integer competitionId; // 关联竞赛ID
    private Integer captainId;     // 队长ID
    private Integer memberCount;   // 队伍成员数量
    private Integer maxMemberCount; // 队伍最大成员数量
    private Integer departmentId;   // 学院ID
    private String teacherId;       // 指导老师ID
    private LocalDateTime createdAt; // 创建时间
    
    // 关联字段
    private String captainName;    // 队长姓名
    private String departmentName; // 学院名称
    private String teacherName;    // 指导老师姓名
    private String competitionName;//竞赛标题
} 