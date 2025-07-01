package com.competition.backend.mapper;

import com.competition.backend.entity.Team;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TeamMapper {
    @Select("SELECT t.*, s.name as captainName, d.name as departmentName, th.name as teacherName " +
            "FROM team t " +
            "LEFT JOIN student s ON t.captain_id = s.student_id " +
            "LEFT JOIN department d ON t.department_id = d.department_id " +
            "LEFT JOIN teacher th ON t.teacher_id = th.teacher_id " +
            "WHERE t.competition_id = #{competitionId}")
    List<Team> selectByCompetitionId(Integer competitionId);
    
    @Insert("INSERT INTO team (name, competition_id, captain_id, member_count, max_member_count, " +
            "department_id, teacher_id, created_at) " +
            "VALUES (#{name}, #{competitionId}, #{captainId}, #{memberCount}, #{maxMemberCount}, " +
            "#{departmentId}, #{teacherId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "teamId")
    void insert(Team team);
    
    @Select("SELECT t.*, s.name as captainName, d.name as departmentName, th.name as teacherName " +
            "FROM team t " +
            "LEFT JOIN student s ON t.captain_id = s.student_id " +
            "LEFT JOIN department d ON t.department_id = d.department_id " +
            "LEFT JOIN teacher th ON t.teacher_id = th.teacher_id " +
            "WHERE t.team_id = #{teamId}")
    Team selectById(Integer teamId);
    
    @Delete("DELETE FROM team WHERE team_id = #{teamId}")
    void deleteById(Integer teamId);
    
    @Update("UPDATE team SET member_count = #{memberCount} WHERE team_id = #{teamId}")
    void updateMemberCount(Team team);
} 
 
 