package com.competition.backend.mapper;

import com.competition.backend.entity.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TeacherMapper {
    /**
     * 根据邮箱查询教师
     * @param email 邮箱
     * @return 教师信息
     */
    @Select("SELECT * FROM teacher WHERE email = #{email}")
    Teacher findByEmail(String email);

    /**
     * 根据ID查询教师
     * @param teacherId 教师ID
     * @return 教师信息
     */
    @Select("SELECT * FROM teacher WHERE teacher_id = #{teacherId}")
    Teacher selectById(Integer teacherId);

    /**
     * 更新教师密码
     * @param teacherId 教师ID
     * @param password 新密码
     */
    @Update("UPDATE teacher SET password = #{password} WHERE teacher_id = #{teacherId}")
    void updatePassword(Integer teacherId, String password);

    /**
     * 根据部门ID查询教师列表
     * @param departmentId 部门ID
     * @return 教师列表
     */
    @Select("SELECT * FROM teacher WHERE department_id = #{departmentId}")
    List<Teacher> selectByDepartmentId(Integer departmentId);
} 