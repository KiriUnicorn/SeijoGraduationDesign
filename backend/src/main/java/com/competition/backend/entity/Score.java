package com.competition.backend.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class Score {
    private Integer scoreId;        // 成绩记录唯一ID
    private Integer teamId;         // 队伍ID
    private Integer teacherId;      // 录入教师ID
    private String awardLevel;      // 获奖等级
    private String certificatePath; // 获奖证书文件路径
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // 录入时间
    
    // 关联字段
    private String teamName;
    private String competitionName;
    private String teacherName;
    private String captainName;
    private List<TeamMember> members;
} 