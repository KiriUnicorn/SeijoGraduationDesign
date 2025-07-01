package com.competition.backend.entity;

import lombok.Data;


public class User {
    private Integer id;
    private String username;
    private String name;
    private String role;
    private String departmentName;
    private Integer departmentId;
    private String email;
    private Integer studentId;
    private Integer teacherId;
    private Integer adminId;
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public User() {
    }

    public User(Integer id, String username, String name, String role, String departmentName, Integer departmentId, String email, Integer studentId, Integer teacherId, Integer adminId, String password) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
        this.departmentName = departmentName;
        this.departmentId = departmentId;
        this.email = email;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.adminId = adminId;
        this.password = password;
    }
}