package com.competition.backend.controller;

import com.competition.backend.common.Result;
import com.competition.backend.entity.Registration;
import com.competition.backend.service.RegistrationService;
import com.competition.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/registrations")
@RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registrationService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public Result<Registration> register(@RequestBody Registration registration, @RequestHeader("Authorization") String token) {
        log.info("学生报名: registration={}", registration);
        try {
            // 从token中获取学生ID
            Integer studentId = jwtUtil.getUserIdFromToken(token);
            if (studentId == null) {
                return Result.error("未找到学生信息");
            }
            
            registration.setStudentId(studentId);
            registration.setStatus("pending");
            
            Registration createdRegistration = registrationService.createRegistration(registration);
            return Result.success(createdRegistration);
        } catch (Exception e) {
            log.error("报名失败", e);
            return Result.error("报名失败");
        }
    }
    
    @PutMapping("/{registrationId}/status")
    public Result<Registration> updateStatus(@PathVariable Integer registrationId, 
                                          @RequestParam String status,
                                          @RequestHeader("Authorization") String token) {
        log.info("更新报名状态: registrationId={}, status={}", registrationId, status);
        try {
            // 从token中获取教师ID
            Integer teacherId = jwtUtil.getUserIdFromToken(token);
            if (teacherId == null) {
                return Result.error("未找到教师信息");
            }
            
            Registration registration = registrationService.getRegistrationById(registrationId);
            if (registration == null) {
                return Result.error("报名记录不存在");
            }
            
            // 检查是否是队伍的指导老师
            if (!registration.getTeacherApproverId().toString().equals(teacherId)) {
                return Result.error("只有指导老师可以审核报名");
            }
            
            registration.setStatus(status);
            registration.setTeacherApproverId(Integer.parseInt(String.valueOf(teacherId)));
            
            Registration updatedRegistration = registrationService.updateRegistration(registration);
            return Result.success(updatedRegistration);
        } catch (Exception e) {
            log.error("更新报名状态失败", e);
            return Result.error("更新状态失败");
        }
    }

    @GetMapping("/teacher/review")
    public Result<List<Registration>> getTeacherReviewList(
            @RequestParam(required = false) String status,
            HttpServletRequest request) {
        log.info("获取教师审核列表 - 开始处理请求");
        
        // 获取并记录token
        String authHeader = request.getHeader("Authorization");
        log.info("Authorization header: {}", authHeader);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.error("无效的Authorization header");
            return Result.error(401, "未登录或token格式错误");
        }
        
        // 提取token
        String token = authHeader.substring(7);
        log.info("提取的token: {}", token);
        
        try {
            // 获取教师ID
            Integer teacherId = jwtUtil.getUserIdFromToken(token);
            log.info("从token中获取的教师ID: {}", teacherId);
            
            if (teacherId == null) {
                log.error("无法从token中获取教师ID");
                return Result.error(401, "无效的token");
            }
            
            // 获取审核列表
            List<Registration> registrations = registrationService.getTeacherReviewList(teacherId, status);
            log.info("成功获取审核列表，数量: {}", registrations.size());
            return Result.success(registrations);
        } catch (Exception e) {
            log.error("获取教师审核列表失败", e);
            return Result.error(500, "获取审核列表失败: " + e.getMessage());
        }
    }

    @PostMapping("/{registrationId}/approve")
    public Result<Void> approveRegistration(
            @PathVariable Integer registrationId,
            HttpServletRequest request) {
        log.info("开始处理审核通过请求 - 注册ID: {}", registrationId);
        
        try {
            String token = request.getHeader("Authorization").substring(7);
            Integer teacherId = jwtUtil.getUserIdFromToken(token);
            
            if (teacherId == null) {
                return Result.error(401, "无效的token");
            }
            
            registrationService.approveRegistration(registrationId, teacherId);
            return Result.success();
        } catch (Exception e) {
            log.error("审核通过失败", e);
            return Result.error(500, "审核通过失败: " + e.getMessage());
        }
    }

    @PostMapping("/{registrationId}/reject")
    public Result<Void> rejectRegistration(
            @PathVariable Integer registrationId,
            HttpServletRequest request) {
        log.info("开始处理审核拒绝请求 - 注册ID: {}", registrationId);
        
        try {
            String token = request.getHeader("Authorization").substring(7);
            Integer teacherId = jwtUtil.getUserIdFromToken(token);
            
            if (teacherId == null) {
                return Result.error(401, "无效的token");
            }
            
            registrationService.rejectRegistration(registrationId, teacherId);
            return Result.success();
        } catch (Exception e) {
            log.error("审核拒绝失败", e);
            return Result.error(500, "审核拒绝失败: " + e.getMessage());
        }
    }
} 