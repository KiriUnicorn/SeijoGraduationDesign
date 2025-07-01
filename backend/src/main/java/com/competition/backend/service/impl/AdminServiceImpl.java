package com.competition.backend.service.impl;

import com.competition.backend.dto.LoginRequest;
import com.competition.backend.entity.Admin;
import com.competition.backend.exception.BusinessException;
import com.competition.backend.mapper.AdminMapper;
import com.competition.backend.service.AdminService;
import com.competition.backend.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String login(LoginRequest request) {
        log.info("开始管理员登录验证: {}", request.getEmail());
        Admin admin = adminMapper.findByEmail(request.getEmail());
        if (admin == null) {
            log.error("管理员不存在: {}", request.getEmail());
            throw new BusinessException("邮箱或密码错误");
        }
        
        log.info("找到管理员记录: {}", admin.getEmail());
        log.info("输入的密码: {}", request.getPassword());
        log.info("数据库中的密码: {}", admin.getPassword());
        
        boolean matches = passwordEncoder.matches(request.getPassword(), admin.getPassword());
        log.info("密码验证结果: {}", matches);
        
        if (!matches) {
            log.error("密码验证失败");
            throw new BusinessException("邮箱或密码错误");
        }

        log.info("密码验证成功，生成token");
        return jwtUtil.generateToken(admin.getEmail(), "admin", admin.getAdminId(),admin.getName(), admin.getAdminId());
    }
} 