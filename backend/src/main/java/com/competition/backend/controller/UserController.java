package com.competition.backend.controller;

import com.alibaba.excel.EasyExcel;
import com.competition.backend.entity.User;
import com.competition.backend.excel.UserImportData;
import com.competition.backend.excel.UserImportListener;
import com.competition.backend.service.UserService;
import com.competition.backend.vo.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public Result<List<User>> getAllUsers() {
        try {
            List<User> users = userService.findAllUsers();
            log.info("获取用户列表成功，共 {} 条记录", users.size());
            return Result.success(users);
        } catch (Exception e) {
            log.error("获取用户列表失败", e);
            return Result.error("获取用户列表失败：" + e.getMessage());
        }
    }

    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            log.info("创建用户成功: {}", createdUser);
            return Result.success(createdUser);
        } catch (Exception e) {
            log.error("创建用户失败", e);
            return Result.error("创建用户失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        try {
            user.setId(id);
            User updatedUser = userService.updateUser(user);
            log.info("更新用户成功: {}", updatedUser);
            return Result.success(updatedUser);
        } catch (Exception e) {
            log.error("更新用户失败", e);
            return Result.error("更新用户失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Integer id, @RequestParam String role) {
        try {
            userService.deleteUser(id, role);
            log.info("删除用户成功: id={}, role={}", id, role);
            return Result.success();
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return Result.error("删除用户失败：" + e.getMessage());
        }
    }

    @PostMapping("/batch")
    public Result<Void> batchImport(@RequestParam("file") MultipartFile file, @RequestParam String role) {
        try {
            if (file.isEmpty()) {
                return Result.error("请选择要导入的文件");
            }

            if (!"student".equals(role) && !"teacher".equals(role)) {
                return Result.error("不支持该角色的批量导入");
            }

            UserImportListener listener = new UserImportListener(userService, passwordEncoder, role);
            EasyExcel.read(file.getInputStream(), UserImportData.class, listener).sheet().doRead();

            List<String> errors = listener.getErrorList();
            if (!errors.isEmpty()) {
                return Result.error("部分数据导入失败：" + String.join("; ", errors));
            }

            return Result.success();
        } catch (Exception e) {
            log.error("批量导入用户失败", e);
            return Result.error("批量导入失败：" + e.getMessage());
        }
    }

    @GetMapping("/template")
    public ResponseEntity<Resource> downloadTemplate(@RequestParam String role) {
        System.out.println("目前角色："+role);
        String templatePath;
        if ("学生".equals(role)) {
            templatePath = "templates/student_import_template.xlsx";
        } else if ("教师".equals(role)) {
            templatePath = "templates/teacher_import_template.xlsx";
        } else {
            throw new IllegalArgumentException("不支持该角色的模板下载");
        }

        try {
            Resource resource = new ClassPathResource(templatePath);
            if (!resource.exists()) {
                throw new FileNotFoundException("模板文件不存在");
            }
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);
        } catch (IOException e) {
            log.error("下载模板文件失败", e);
            throw new RuntimeException("下载模板文件失败");
        }
    }
}

class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
} 
 