package com.competition.backend.service;

import com.competition.backend.common.Result;
import com.competition.backend.dto.LoginRequest;
import com.competition.backend.dto.LoginResponse;
import com.competition.backend.entity.Teacher;
import java.util.List;

public interface TeacherService {
    /**
     * 教师登录
     * @param request 登录请求
     * @return 登录响应
     */
    String login(LoginRequest request);

    /**
     * 根据部门ID获取教师列表
     * @param departmentId 部门ID
     * @return 教师列表
     */
    List<Teacher> getTeachersByDepartment(Integer departmentId);
} 