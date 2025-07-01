package com.competition.backend.entity;

import lombok.Data;

import java.util.Date;
@Data
public class Registration {
    private Integer registrationId;    // 报名记录唯一ID
    private Integer studentId;         // 报名学生ID
    private Integer competitionId;     // 竞赛ID
    private String status;             // 审核状态
    private Integer teacherApproverId; // 审批教师ID
    private Date createdAt;           // 创建时间
    private Integer teamId;            // 队伍ID（外键team表）
    
    // 扩展字段，用于前端显示
    private String competitionName;
    private String teamName;
    private String captainName;
    private String memberNames;
    private String teacherName;
    private String registrationName;

    // Getters and Setters
    public Integer getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Integer registrationId) {
        this.registrationId = registrationId;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Integer competitionId) {
        this.competitionId = competitionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTeacherApproverId() {
        return teacherApproverId;
    }

    public void setTeacherApproverId(Integer teacherApproverId) {
        this.teacherApproverId = teacherApproverId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public void setTeamId(Integer teamId) {
        this.teamId = teamId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getCaptainName() {
        return captainName;
    }

    public void setCaptainName(String captainName) {
        this.captainName = captainName;
    }

    public String getMemberNames() {
        return memberNames;
    }

    public void setMemberNames(String memberNames) {
        this.memberNames = memberNames;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
} 