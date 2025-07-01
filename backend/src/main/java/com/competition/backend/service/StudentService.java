package com.competition.backend.service;

import com.competition.backend.common.Result;
import com.competition.backend.dto.LoginRequest;
import com.competition.backend.dto.LoginResponse;
import com.competition.backend.entity.Student;

public interface StudentService {
    /**
     * 学生登录
     * @param request 登录请求
     * @return 登录响应
     */
    String login(LoginRequest request);
} 