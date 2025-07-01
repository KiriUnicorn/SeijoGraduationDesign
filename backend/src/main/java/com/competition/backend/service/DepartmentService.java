package com.competition.backend.service;

import com.competition.backend.entity.Department;
import java.util.List;

public interface DepartmentService {
    List<Department> findById(Integer id);
    List<Department> findAllDepartments();
    Department createDepartment(Department department);
    Department updateDepartment(Department department);
    void deleteDepartment(Integer id);
} 