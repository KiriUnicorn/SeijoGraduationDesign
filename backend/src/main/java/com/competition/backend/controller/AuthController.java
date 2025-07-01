package com.competition.backend.controller;

import com.competition.backend.common.Result;
import com.competition.backend.dto.LoginRequest;
import com.competition.backend.dto.LoginResponse;
import com.competition.backend.entity.Admin;
import com.competition.backend.entity.Student;
import com.competition.backend.entity.Teacher;
import com.competition.backend.service.AdminService;
import com.competition.backend.service.StudentService;
import com.competition.backend.service.TeacherService;
import com.competition.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AdminService adminService;
    private final TeacherService teacherService;
    private final StudentService studentService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/admin/login")
    public Result<String> adminLogin(@RequestBody LoginRequest request) {
        log.info("管理员登录请求: {}", request.getEmail());
        try {
            String token = adminService.login(request);
            log.info("管理员登录成功: {}", request.getEmail());
            return Result.success(token);
        } catch (Exception e) {
            log.error("管理员登录失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/teacher/login")
    public Result<String> teacherLogin(@RequestBody LoginRequest request) {
        log.info("教师登录请求: {}", request.getEmail());
        try {
            String token = teacherService.login(request);
            log.info("教师登录成功: {}", request.getEmail());
            return Result.success(token);
        } catch (Exception e) {
            log.error("教师登录失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/student/login")
    public Result<String> studentLogin(@RequestBody LoginRequest request) {
        log.info("学生登录请求: {}", request.getEmail());
        try {
            String token = studentService.login(request);
            log.info("学生登录成功: {}", request.getEmail());
            return Result.success(token);
        } catch (Exception e) {
            log.error("学生登录失败: {}", e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }

    // 临时测试端点
    @GetMapping("/test/password/{rawPassword}")
    public String testPasswordHash(@PathVariable String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
} 