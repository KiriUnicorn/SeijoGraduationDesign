package com.competition.backend.service.impl;

import com.competition.backend.dto.LoginRequest;
import com.competition.backend.entity.Teacher;
import com.competition.backend.exception.BusinessException;
import com.competition.backend.mapper.TeacherMapper;
import com.competition.backend.service.TeacherService;
import com.competition.backend.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TeacherServiceImpl implements TeacherService {
    @Autowired
    private TeacherMapper teacherMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String login(LoginRequest request) {
        log.info("开始教师登录验证: {}", request.getEmail());
        Teacher teacher = teacherMapper.findByEmail(request.getEmail());
        if (teacher == null) {
            log.error("未找到教师记录: {}", request.getEmail());
            throw new BusinessException("邮箱或密码错误");
        }

        log.info("找到教师记录: {}", request.getEmail());
        log.info("输入的密码: {}", request.getPassword());
        log.info("数据库中的密码: {}", teacher.getPassword());
        boolean matches = passwordEncoder.matches(request.getPassword(), teacher.getPassword());
        log.info("密码验证结果: {}", matches);

        if (!matches) {
            log.error("密码验证失败: {}", request.getEmail());
            throw new BusinessException("邮箱或密码错误");
        }

        log.info("密码验证成功，生成token");
        return jwtUtil.generateToken(teacher.getEmail(), "teacher", teacher.getTeacherId(), teacher.getName(), teacher.getDepartmentId());
    }

    @Override
    public List<Teacher> getTeachersByDepartment(Integer departmentId) {
        log.info("获取部门教师列表: departmentId={}", departmentId);
        return teacherMapper.selectByDepartmentId(departmentId);
    }
} 