package com.competition.backend.service.impl;

import com.competition.backend.dto.LoginRequest;
import com.competition.backend.entity.Student;
import com.competition.backend.exception.BusinessException;
import com.competition.backend.mapper.StudentMapper;
import com.competition.backend.service.StudentService;
import com.competition.backend.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String login(LoginRequest request) {
        log.info("开始学生登录验证: {}", request.getEmail());
        Student student = studentMapper.findByEmail(request.getEmail());
        log.info("学生：{}", student.toString());
        if (student == null) {
            log.error("未找到学生记录: {}", request.getEmail());
            throw new BusinessException("邮箱或密码错误");
        }

        log.info("找到学生记录: {}", request.getEmail());
        log.info("输入的密码: {}", request.getPassword());
        log.info("数据库中的密码: {}", student.getPassword());
        boolean matches = passwordEncoder.matches(request.getPassword(), student.getPassword());
        log.info("密码验证结果: {}", matches);

        if (!matches) {
            log.error("密码验证失败: {}", request.getEmail());
            throw new BusinessException("邮箱或密码错误");
        }

        log.info("密码验证成功，生成token");
        return jwtUtil.generateToken(student.getEmail(), "student", student.getStudentId(), student.getName(), student.getDepartmentId());
    }
} 