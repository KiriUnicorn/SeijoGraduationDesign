package com.competition.backend.mapper;

import com.competition.backend.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT " +
            "s.student_id as id, " +
            "s.student_id as username, " +
            "s.name, " +
            "'student' as role, " +
            "d.name as department_name, " +
            "s.email, " +
            "s.student_id as student_id, " +
            "NULL as teacher_id " +
            "FROM student s " +
            "LEFT JOIN department d ON s.department_id = d.department_id " +
            "UNION ALL " +
            "SELECT " +
            "t.teacher_id as id, " +
            "t.teacher_id as username, " +
            "t.name, " +
            "'teacher' as role, " +
            "d.name as department_name, " +
            "t.email, " +
            "NULL as student_id, " +
            "t.teacher_id as teacher_id " +
            "FROM teacher t " +
            "LEFT JOIN department d ON t.department_id = d.department_id " +
            "UNION ALL " +
            "SELECT " +
            "a.admin_id as id, " +
            "a.admin_id as username, " +
            "a.name, " +
            "'admin' as role, " +
            "NULL as department_name, " +
            "a.email, " +
            "NULL as student_id, " +
            "NULL as teacher_id " +
            "FROM admin a")
    List<User> findAllUsers();

    @Update("UPDATE student SET name = #{name}, email = #{email} WHERE student_id = #{id}")
    int updateStudent(User user);

    @Update("UPDATE teacher SET name = #{name}, email = #{email} WHERE teacher_id = #{id}")
    int updateTeacher(User user);

    @Update("UPDATE admin SET name = #{name}, email = #{email} WHERE admin_id = #{id}")
    int updateAdmin(User user);

    @Delete("DELETE FROM student WHERE student_id = #{id}")
    int deleteStudent(Integer id);

    @Delete("DELETE FROM teacher WHERE teacher_id = #{id}")
    int deleteTeacher(Integer id);

    @Delete("DELETE FROM admin WHERE admin_id = #{id}")
    int deleteAdmin(Integer id);

    @Insert("INSERT INTO student (student_id, name, email, password, department_id) " +
            "VALUES (#{studentId}, #{name}, #{email}, #{password}, #{departmentId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertStudent(User user);

    @Insert("INSERT INTO teacher (teacher_id, name, email, password, department_id) " +
            "VALUES (#{teacherId}, #{name}, #{email}, #{password}, #{departmentId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertTeacher(User user);

    @Insert("INSERT INTO admin (name, email, password) " +
            "VALUES (#{name}, #{email}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertAdmin(User user);

    @Select("select * from student where student_id=#{studentId}")
    User findByStudentId(Integer studentId);
}