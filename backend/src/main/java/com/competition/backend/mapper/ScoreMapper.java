package com.competition.backend.mapper;

import com.competition.backend.entity.Score;
import com.competition.backend.entity.Team;
import com.competition.backend.entity.TeamMember;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ScoreMapper {
    
    @Select("SELECT s.*, t.name as team_name, c.title as competition_name, tc.name as teacher_name " +
            "FROM score s " +
            "LEFT JOIN team t ON s.team_id = t.team_id " +
            "LEFT JOIN competition c ON t.competition_id = c.competition_id " +
            "LEFT JOIN teacher tc ON s.teacher_id = tc.teacher_id " +
            "WHERE s.teacher_id = #{teacherId}")
    List<Score> findByTeacherId(Integer teacherId);
    
    @Select("SELECT s.*, t.name as team_name, c.title as competition_name, tc.name as teacher_name " +
            "FROM score s " +
            "LEFT JOIN team t ON s.team_id = t.team_id " +
            "LEFT JOIN competition c ON t.competition_id = c.competition_id " +
            "LEFT JOIN teacher tc ON s.teacher_id = tc.teacher_id " +
            "WHERE s.team_id = #{teamId}")
    Score findByTeamId(Integer teamId);
    
    @Insert("INSERT INTO score (team_id, teacher_id, award_level, certificate_path, created_at) " +
            "VALUES (#{teamId}, #{teacherId}, #{awardLevel}, #{certificatePath}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "scoreId")
    int insert(Score score);
    
    @Update("UPDATE score SET award_level = #{awardLevel}, certificate_path = #{certificatePath} " +
            "WHERE score_id = #{scoreId}")
    int update(Score score);
    
    @Select("SELECT t.*, c.title as competition_name, " +
            "s.name as captain_name, " +
            "d.name as department_name, " +
            "tc.name as teacher_name " +
            "FROM team t " +
            "LEFT JOIN competition c ON t.competition_id = c.competition_id " +
            "LEFT JOIN student s ON t.captain_id = s.student_id " +
            "LEFT JOIN department d ON t.department_id = d.department_id " +
            "LEFT JOIN teacher tc ON t.teacher_id = tc.teacher_id " +
            "WHERE t.teacher_id = #{teacherId} " +
            "AND NOT EXISTS (SELECT 1 FROM score s WHERE s.team_id = t.team_id)")
    List<Team> findTeamsWithoutScore(Integer teacherId);
    
    @Select("SELECT s.*, t.name as team_name, c.title as competition_name, " +
            "tc.name as teacher_name, st.name as captain_name " +
            "FROM score s " +
            "LEFT JOIN team t ON s.team_id = t.team_id " +
            "LEFT JOIN student st ON t.captain_id = st.student_id " +
            "LEFT JOIN competition c ON t.competition_id = c.competition_id " +
            "LEFT JOIN teacher tc ON s.teacher_id = tc.teacher_id " +
            "WHERE s.score_id = #{scoreId}")
    @Results({
        @Result(property = "scoreId", column = "score_id"),
        @Result(property = "teamId", column = "team_id"),
        @Result(property = "teacherId", column = "teacher_id"),
        @Result(property = "awardLevel", column = "award_level"),
        @Result(property = "certificatePath", column = "certificate_path"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "teamName", column = "team_name"),
        @Result(property = "competitionName", column = "competition_name"),
        @Result(property = "teacherName", column = "teacher_name"),
        @Result(property = "captainName", column = "captain_name")
    })
    Score findById(Integer scoreId);


    @Select("SELECT tm.*, s.name as student_name " +
            "FROM score sc " +
            "JOIN team_member tm ON sc.team_id = tm.team_id " +
            "JOIN student s ON tm.student_id = s.student_id " +
            "WHERE sc.score_id = #{scoreId}")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "teamId", column = "team_id"),
            @Result(property = "studentId", column = "student_id"),
            @Result(property = "studentName", column = "student_name"),
            @Result(property = "joinedAt", column = "joined_at")
    })
    List<TeamMember> findTeamMembersByScoreId(@Param("scoreId") Integer scoreId);

    @Select("SELECT s.*, c.title as competition_name, t.name as team_name, " +
            "u.name as captain_name, u2.name as teacher_name " +
            "FROM score s " +
            "LEFT JOIN team t ON s.team_id = t.team_id " +
            "LEFT JOIN competition c ON t.competition_id = c.competition_id " +
            "LEFT JOIN student u ON t.captain_id = u.student_id " +
            "LEFT JOIN teacher u2 ON s.teacher_id = u2.teacher_id " +
            "INNER JOIN team_member tm ON s.team_id = tm.team_id " +
            "WHERE tm.student_id = #{studentId} " +
            "ORDER BY s.created_at DESC")
    @Results({
        @Result(property = "scoreId", column = "score_id"),
        @Result(property = "teamId", column = "team_id"),
        @Result(property = "teacherId", column = "teacher_id"),
        @Result(property = "awardLevel", column = "award_level"),
        @Result(property = "certificatePath", column = "certificate_path"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at"),
        @Result(property = "competitionName", column = "competition_name"),
        @Result(property = "teamName", column = "team_name"),
        @Result(property = "captainName", column = "captain_name"),
        @Result(property = "teacherName", column = "teacher_name")
    })
    List<Score> findScoresByStudentId(Integer studentId);
} 