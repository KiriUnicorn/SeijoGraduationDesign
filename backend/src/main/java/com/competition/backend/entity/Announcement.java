package com.competition.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Announcement {
    private Integer announcementId;  // 公告唯一ID
    private Integer adminId;
    
    /**
     * 管理员姓名
     */
    private String adminName;
    
    private String title;            // 公告标题
    private String content;          // 公告正文
    private LocalDateTime createdAt; // 发布时间
} 