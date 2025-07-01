package com.competition.backend.mapper;

import com.competition.backend.entity.Department;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DepartmentMapper {

    @Select("SELECT * FROM department where department_id = #{id}")
    List<Department> findById(Integer id);

    @Select("SELECT * FROM department")
    List<Department> findAll();

    @Insert("INSERT INTO department (name, contact) VALUES (#{name}, #{contact})")
    @Options(useGeneratedKeys = true, keyProperty = "departmentId")
    int insert(Department department);

    @Update("UPDATE department SET name = #{name}, contact = #{contact} WHERE department_id = #{departmentId}")
    int update(Department department);

    @Delete("DELETE FROM department WHERE department_id = #{id}")
    int deleteById(Integer id);
} 