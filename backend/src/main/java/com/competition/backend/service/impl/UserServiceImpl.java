package com.competition.backend.service.impl;

import com.competition.backend.entity.Admin;
import com.competition.backend.entity.Student;
import com.competition.backend.entity.Teacher;
import com.competition.backend.entity.User;
import com.competition.backend.mapper.AdminMapper;
import com.competition.backend.mapper.StudentMapper;
import com.competition.backend.mapper.TeacherMapper;
import com.competition.backend.mapper.UserMapper;
import com.competition.backend.service.UserService;
import com.competition.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AdminMapper adminMapper;
    private final TeacherMapper teacherMapper;
    private final StudentMapper studentMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public void changePassword(String token, String oldPassword, String newPassword) {
        // 从token中获取用户ID和角色
        Integer userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token).toUpperCase();  // 转换为大写
        log.info("修改密码: userId={}, role={}", userId, role);

        // 验证原密码
        boolean isOldPasswordCorrect = false;
        switch (role) {
            case "ADMIN":
                Admin admin = adminMapper.selectById(userId);
                if (admin == null) {
                    log.error("未找到管理员记录: userId={}", userId);
                    throw new RuntimeException("用户不存在");
                }
                log.info("管理员原密码: {}", admin.getPassword());
                isOldPasswordCorrect = passwordEncoder.matches(oldPassword, admin.getPassword());
                log.info("密码验证结果: {}", isOldPasswordCorrect);
                break;
            case "TEACHER":
                Teacher teacher = teacherMapper.selectById(userId);
                if (teacher == null) {
                    log.error("未找到教师记录: userId={}", userId);
                    throw new RuntimeException("用户不存在");
                }
                log.info("教师原密码: {}", teacher.getPassword());
                isOldPasswordCorrect = passwordEncoder.matches(oldPassword, teacher.getPassword());
                log.info("密码验证结果: {}", isOldPasswordCorrect);
                break;
            case "STUDENT":
                Student student = studentMapper.selectById(userId);
                if (student == null) {
                    log.error("未找到学生记录: userId={}", userId);
                    throw new RuntimeException("用户不存在");
                }
                log.info("学生原密码: {}", student.getPassword());
                isOldPasswordCorrect = passwordEncoder.matches(oldPassword, student.getPassword());
                log.info("密码验证结果: {}", isOldPasswordCorrect);
                break;
            default:
                log.error("未知的用户角色: {}", role);
                throw new RuntimeException("未知的用户角色");
        }

        if (!isOldPasswordCorrect) {
            log.error("原密码验证失败: userId={}, role={}", userId, role);
            throw new RuntimeException("原密码错误");
        }

        // 加密新密码
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        log.info("加密后的新密码: {}", encodedNewPassword);

        // 更新密码
        try {
            switch (role) {
                case "ADMIN":
                    adminMapper.updatePassword(userId, encodedNewPassword);
                    break;
                case "TEACHER":
                    teacherMapper.updatePassword(userId, encodedNewPassword);
                    break;
                case "STUDENT":
                    studentMapper.updatePassword(userId, encodedNewPassword);
                    break;
            }
            log.info("密码更新成功: userId={}, role={}", userId, role);
        } catch (Exception e) {
            log.error("更新密码失败: userId={}, role={}", userId, role, e);
            throw new RuntimeException("更新密码失败");
        }
    }

    @Override
    public List<User> findAllUsers() {
        return userMapper.findAllUsers();
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        log.info("更新用户信息: {}", user);
        
        int rows = 0;
        switch (user.getRole().toLowerCase()) {
            case "student":
                rows = userMapper.updateStudent(user);
                break;
            case "teacher":
                rows = userMapper.updateTeacher(user);
                break;
            case "admin":
                rows = userMapper.updateAdmin(user);
                break;
            default:
                throw new RuntimeException("未知的用户角色: " + user.getRole());
        }

        if (rows == 0) {
            throw new RuntimeException("用户不存在或更新失败");
        }

        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Integer id, String role) {
        log.info("删除用户: id={}, role={}", id, role);
        
        int rows = 0;
        switch (role.toLowerCase()) {
            case "student":
                rows = userMapper.deleteStudent(id);
                break;
            case "teacher":
                rows = userMapper.deleteTeacher(id);
                break;
            case "admin":
                rows = userMapper.deleteAdmin(id);
                break;
            default:
                throw new RuntimeException("未知的用户角色: " + role);
        }

        if (rows == 0) {
            throw new RuntimeException("用户不存在或删除失败");
        }
    }

    @Override
    @Transactional
    public User createUser(User user) {
        log.info("创建用户: {}", user);
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 根据角色选择不同的mapper
        int rows;
        switch (user.getRole()) {
            case "student":
                rows = userMapper.insertStudent(user);
                break;
            case "teacher":
                rows = userMapper.insertTeacher(user);
                break;
            case "admin":
                rows = userMapper.insertAdmin(user);
                break;
            default:
                throw new IllegalArgumentException("未知的用户角色: " + user.getRole());
        }
        
        if (rows != 1) {
            throw new RuntimeException("创建用户失败");
        }
        
        return user;
    }
} 