package com.competition.backend.controller;

import com.competition.backend.entity.Department;
import com.competition.backend.service.DepartmentService;
import com.competition.backend.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public Result<List<Department>> getAllDepartments() {
        log.info("获取所有院系信息");
        try {
            List<Department> departments = departmentService.findAllDepartments();
            return Result.success(departments);
        } catch (Exception e) {
            log.error("获取院系列表失败", e);
            return Result.error("获取院系列表失败");
        }
    }

    @GetMapping("/{id}")
    public Result<List<Department>> getDepartmentById(Integer id){
        log.info("根据id获取院系信息");
        try {
            List<Department> departments = departmentService.findById(id);
            return Result.success(departments);
        } catch (Exception e) {
            log.error("根据id获取院系信息失败", e);
            return Result.error("获取院系列表失败");
        }
    }

    @PostMapping
    public Result<Department> createDepartment(@RequestBody Department department) {
        log.info("创建院系: {}", department);
        try {
            Department created = departmentService.createDepartment(department);
            return Result.success(created);
        } catch (Exception e) {
            log.error("创建院系失败", e);
            return Result.error("创建院系失败");
        }
    }

    @PutMapping("/{id}")
    public Result<Department> updateDepartment(@PathVariable Integer id, @RequestBody Department department) {
        log.info("更新院系: id={}, department={}", id, department);
        try {
            department.setDepartmentId(id);
            Department updated = departmentService.updateDepartment(department);
            return Result.success(updated);
        } catch (Exception e) {
            log.error("更新院系失败", e);
            return Result.error("更新院系失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteDepartment(@PathVariable Integer id) {
        log.info("删除院系: id={}", id);
        try {
            departmentService.deleteDepartment(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除院系失败", e);
            return Result.error("删除院系失败");
        }
    }
} 