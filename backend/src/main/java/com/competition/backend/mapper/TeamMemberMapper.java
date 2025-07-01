package com.competition.backend.mapper;

import com.competition.backend.entity.TeamMember;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Result;

import java.util.List;

@Mapper
public interface TeamMemberMapper {
    @Insert("INSERT INTO team_member (team_id, student_id) VALUES (#{teamId}, #{studentId})")
    int insert(@Param("teamId") Integer teamId, @Param("studentId") Integer studentId);

    @Select("SELECT tm.*, s.name, s.student_id " +
            "FROM team_member tm " +
            "LEFT JOIN student s ON tm.student_id = s.student_id " +
            "WHERE tm.team_id = #{teamId}")
    @Results({
        @Result(property = "teamMemberId", column = "team_member_id"),
        @Result(property = "teamId", column = "team_id"),
        @Result(property = "studentId", column = "student_id"),
        @Result(property = "name", column = "name"),
        @Result(property = "isCaptain", column = "is_captain")
    })
    List<TeamMember> findByTeamId(Integer teamId);
} 