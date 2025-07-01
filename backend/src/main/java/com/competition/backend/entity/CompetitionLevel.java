package com.competition.backend.entity;

import lombok.Data;

@Data
public class CompetitionLevel {
    private Integer levelId;     // 竞赛等级唯一ID
    private String levelName;    // 等级名称
} 