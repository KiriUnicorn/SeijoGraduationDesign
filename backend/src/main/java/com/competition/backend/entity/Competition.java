package com.competition.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Competition {
    private Integer competitionId;           // 竞赛唯一ID
    private String title;                    // 竞赛标题
    private String description;              // 竞赛详细描述
    private Integer hostUnitId;              // 主办单位ID
    private String hostUnitName;             // 主办单位名称
    private Integer responsibleDepartmentId; // 责任院系ID
    private String departmentName;           // 责任院系名称
    private Integer levelId;                 // 竞赛等级ID
    private String levelName;                // 竞赛等级名称
    private LocalDateTime signupStartTime;   // 报名开始时间
    private LocalDateTime signupEndTime;     // 报名结束时间
    private LocalDateTime competitionStartTime; // 竞赛开始时间
    private LocalDateTime competitionEndTime;   // 竞赛结束时间
    private String status;                   // 竞赛状态
    private Integer adminCreatorId;          // 管理员创建者ID
    private String adminCreatorName;          // 管理员创建者ID
    private Integer teacherCreatorId;        // 教师创建者ID
    private LocalDateTime createdAt;         // 创建时间
    private Integer currentParticipants;     // 当前报名人数
    private Integer maxParticipants;         // 最大报名人数

    //扩展字段
    private String competitionTitle;
    private String registrationStatus;
    private String teamName;
    private String teacherName;
} 