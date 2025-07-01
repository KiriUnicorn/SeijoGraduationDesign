package com.competition.backend.service.impl;

import com.competition.backend.entity.Department;
import com.competition.backend.mapper.DepartmentMapper;
import com.competition.backend.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private final DepartmentMapper departmentMapper;

    @Override
    public List<Department> findById(Integer id) {
        return departmentMapper.findById(id);
    }

    @Override
    public List<Department> findAllDepartments() {
        return departmentMapper.findAll();
    }

    @Override
    @Transactional
    public Department createDepartment(Department department) {
        departmentMapper.insert(department);
        return department;
    }

    @Override
    @Transactional
    public Department updateDepartment(Department department) {
        int rows = departmentMapper.update(department);
        if (rows == 0) {
            throw new RuntimeException("院系不存在或更新失败");
        }
        return department;
    }

    @Override
    @Transactional
    public void deleteDepartment(Integer id) {
        int rows = departmentMapper.deleteById(id);
        if (rows == 0) {
            throw new RuntimeException("院系不存在或删除失败");
        }
    }
} 