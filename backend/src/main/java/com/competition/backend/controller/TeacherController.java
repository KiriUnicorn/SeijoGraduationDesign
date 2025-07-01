package com.competition.backend.controller;

import com.competition.backend.common.Result;
import com.competition.backend.entity.Teacher;
import com.competition.backend.service.TeacherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/teachers")
@CrossOrigin
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping("/department/{departmentId}")
    public Result<List<Teacher>> getTeachersByDepartment(@PathVariable Integer departmentId) {
        log.info("获取部门教师列表: departmentId={}", departmentId);
        List<Teacher> teachers = teacherService.getTeachersByDepartment(departmentId);
        return Result.success(teachers);
    }
} 
 
 