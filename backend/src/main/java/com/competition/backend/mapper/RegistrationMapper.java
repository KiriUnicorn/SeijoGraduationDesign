package com.competition.backend.mapper;

import com.competition.backend.entity.Registration;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface RegistrationMapper {
    @Insert("INSERT INTO registration (competition_id, student_id, team_id, status, teacher_approver_id, created_at) " +
            "VALUES (#{competitionId}, #{studentId}, #{teamId}, #{status}, " +
            "(SELECT teacher_id FROM team WHERE team_id = #{teamId}), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "registrationId")
    int insert(Registration registration);

    @Select("SELECT * FROM registration WHERE registration_id = #{registrationId}")
    Registration selectById(Integer registrationId);

    @Update("UPDATE registration SET status = #{status}, teacher_approver_id = #{teacherApproverId} " +
            "WHERE registration_id = #{registrationId}")
    int update(Registration registration);

    @Insert("INSERT INTO team_member (student_id, team_id) VALUES (#{studentId}, #{teamId})")
    int insertTeamMember(@Param("studentId") Integer studentId, @Param("teamId") Integer teamId);

    @Select("SELECT COUNT(*) FROM registration WHERE competition_id = #{competitionId}")
    Integer countByCompetitionId(Integer competitionId);

    @Select({
            "SELECT",
            "    r.registration_id,",
            "    r.student_id,",
            "    r.competition_id,",
            "    r.status,",
            "    r.teacher_approver_id,",
            "    r.created_at,",
            "    r.team_id,",
            "    c.title AS competition_name,",
            "    t.name AS team_name,",
            "    s.name AS captain_name,",
            "    reg_student.name AS registration_name,",
            "    GROUP_CONCAT(DISTINCT s2.name) AS member_names,",
            "    t2.name AS teacher_name",
            "FROM",
            "    registration r",
            "LEFT JOIN",
            "    competition c ON r.competition_id = c.competition_id",
            "LEFT JOIN",
            "    team t ON r.team_id = t.team_id",
            "LEFT JOIN",
            "    student s ON t.captain_id = s.student_id",
            "LEFT JOIN",
            "    team_member tm ON t.team_id = tm.team_id",
            "LEFT JOIN",
            "    student s2 ON tm.student_id = s2.student_id",
            "LEFT JOIN",
            "    teacher t2 ON r.teacher_approver_id = t2.teacher_id",
            "LEFT JOIN",
            "    student reg_student ON r.student_id = reg_student.student_id",
            "WHERE",
            "    r.teacher_approver_id = #{teacherId}",
            "GROUP BY",
            "    r.registration_id,",
            "    r.student_id,",
            "    r.competition_id,",
            "    r.status,",
            "    r.teacher_approver_id,",
            "    r.created_at,",
            "    r.team_id,",
            "    c.title,",
            "    t.name,",
            "    s.name,",
            "    reg_student.name,",
            "    t2.name",
            "ORDER BY",
            "    r.created_at DESC"
    })
    List<Registration> getTeacherReviewList(@Param("teacherId") Integer teacherId, @Param("status") String status);

    @Update("UPDATE registration SET status = #{status}, teacher_approver_id = #{teacherApproverId} " +
            "WHERE registration_id = #{registrationId}")
    int updateStatus(@Param("registrationId") Integer registrationId,
                    @Param("status") String status,
                    @Param("teacherApproverId") Integer teacherApproverId);

    @Select("SELECT " +
            "r.registration_id, r.student_id, r.competition_id, r.status, " +
            "r.teacher_approver_id, r.created_at, r.team_id, " +
            "c.title as competition_name, " +
            "t.name as team_name, " +
            "s.name as captain_name, " +
            "GROUP_CONCAT(s2.name) as member_names, " +
            "t2.name as teacher_name " +
            "FROM registration r " +
            "LEFT JOIN competition c ON r.competition_id = c.competition_id " +
            "LEFT JOIN team t ON r.team_id = t.team_id " +
            "LEFT JOIN student s ON t.captain_id = s.student_id " +
            "LEFT JOIN team_member tm ON t.team_id = tm.team_id " +
            "LEFT JOIN student s2 ON tm.student_id = s2.student_id " +
            "LEFT JOIN teacher t2 ON r.teacher_approver_id = t2.teacher_id " +
            "WHERE r.registration_id = #{registrationId} " +
            "GROUP BY r.registration_id")
    Registration getRegistrationDetail(@Param("registrationId") Integer registrationId);

    @Update("UPDATE registration SET status = #{status} WHERE registration_id = #{registrationId} AND teacher_approver_id = #{teacherId}")
    int updateRegistrationStatus(@Param("registrationId") Integer registrationId, @Param("status") String status, @Param("teacherId") Long teacherId);

    @Insert("INSERT INTO team_member (student_id, team_id) VALUES (#{studentId}, #{teamId})")
    int addTeamMember(@Param("studentId") Integer studentId, @Param("teamId") Long teamId);

    @Select({
            "SELECT ",
            "    d.name AS 院系名称,",
            "    COUNT(DISTINCT s.student_id) AS 总学生数,",
            "    COUNT(DISTINCT r.student_id) AS 已报名学生数,",
            "    COUNT(r.registration_id) AS 总报名次数,",
            "    ROUND(COUNT(DISTINCT r.student_id) * 100.0 / NULLIF(COUNT(DISTINCT s.student_id), 0), 2) AS 报名率",
            "FROM ",
            "    department d",
            "LEFT JOIN ",
            "    student s ON d.department_id = s.department_id",
            "LEFT JOIN ",
            "    registration r ON s.student_id = r.student_id",
            "GROUP BY ",
            "    d.department_id, d.name",
            "ORDER BY ",
            "    总学生数 DESC"
    })
    List<Map<String, Object>> getDepartmentRegistrationStats();

    @Select({
            "SELECT ",
            "    SUBSTRING(s.student_id, 1, 4) AS 年级,",
            "    COUNT(DISTINCT s.student_id) AS 总人数,",
            "    COUNT(DISTINCT r.student_id) AS 报名人数,",
            "    COUNT(r.registration_id) AS 报名总次数,",
            "    ROUND(COUNT(DISTINCT r.student_id) * 100.0 / NULLIF(COUNT(DISTINCT s.student_id), 0), 2) AS 报名率",
            "FROM ",
            "    student s",
            "LEFT JOIN ",
            "    registration r ON s.student_id = r.student_id",
            "GROUP BY ",
            "    SUBSTRING(s.student_id, 1, 4)",
            "ORDER BY ",
            "    年级 DESC"
    })
    List<Map<String, Object>> getGradeRegistrationStats();

}

