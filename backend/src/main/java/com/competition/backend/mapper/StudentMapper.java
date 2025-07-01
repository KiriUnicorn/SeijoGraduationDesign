package com.competition.backend.mapper;

import com.competition.backend.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface StudentMapper {
    /**
     * 根据邮箱查询学生
     * @param email 邮箱
     * @return 学生信息
     */
    @Select("SELECT * FROM student WHERE email = #{email}")
    Student findByEmail(String email);

    /**
     * 根据ID查询学生
     * @param studentId 学生ID
     * @return 学生信息
     */
    @Select("SELECT * FROM student WHERE student_id = #{studentId}")
    Student selectById(Integer studentId);

    /**
     * 更新学生密码
     * @param studentId 学生ID
     * @param password 新密码
     */
    @Update("UPDATE student SET password = #{password} WHERE student_id = #{studentId}")
    void updatePassword(Integer studentId, String password);
} 