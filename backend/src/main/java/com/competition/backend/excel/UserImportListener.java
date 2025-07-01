package com.competition.backend.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.competition.backend.entity.User;
import com.competition.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserImportListener extends AnalysisEventListener<UserImportData> {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final String role;
    private final List<User> successList = new ArrayList<>();
    private final List<String> errorList = new ArrayList<>();
    
    public UserImportListener(UserService userService, PasswordEncoder passwordEncoder, String role) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.role = role;
    }
    
    @Override
    public void invoke(UserImportData data, AnalysisContext context) {
        try {
            // 转换为User实体
            User user = new User();
            user.setName(data.getName());
            user.setEmail(data.getEmail());
            user.setRole(role);
            user.setPassword("123456"); // 默认密码
            
            if ("student".equals(role)) {
                user.setStudentId(data.getStudentId());
            } else if ("teacher".equals(role)) {
                user.setTeacherId(data.getTeacherId());
            }
            
            user.setDepartmentId(data.getDepartmentId());
            
            // 保存用户
            userService.createUser(user);
            successList.add(user);
        } catch (Exception e) {
            log.error("导入用户失败: {}", data, e);
            errorList.add(String.format("导入失败: %s, 原因: %s", data.getName(), e.getMessage()));
        }
    }
    
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("导入完成，成功: {}条，失败: {}条", successList.size(), errorList.size());
    }
    
    public List<User> getSuccessList() {
        return successList;
    }
    
    public List<String> getErrorList() {
        return errorList;
    }
} 