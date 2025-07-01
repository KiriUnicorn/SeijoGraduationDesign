package com.competition.backend.service.impl;

import com.competition.backend.entity.Team;
import com.competition.backend.entity.Teacher;
import com.competition.backend.mapper.TeamMapper;
import com.competition.backend.mapper.TeacherMapper;
import com.competition.backend.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamServiceImpl implements TeamService {
    
    @Autowired
    private TeamMapper teamMapper;
    
    @Autowired
    private TeacherMapper teacherMapper;
    
    @Override
    public List<Team> getTeamsByCompetition(Integer competitionId) {
        return teamMapper.selectByCompetitionId(competitionId);
    }
    
    @Override
    @Transactional
    public Team createTeam(Team team) {
        // 如果选择了指导教师，获取其部门ID
        if (team.getTeacherId() != null && !team.getTeacherId().isEmpty()) {
            Teacher teacher = teacherMapper.selectById(Integer.parseInt(team.getTeacherId()));
            if (teacher != null) {
                team.setDepartmentId(teacher.getDepartmentId());
            }
        }
        
        teamMapper.insert(team);
        return team;
    }
    
    @Override
    public Team getTeamById(Integer teamId) {
        return teamMapper.selectById(teamId);
    }
    
    @Override
    @Transactional
    public void deleteTeam(Integer teamId) {
        teamMapper.deleteById(teamId);
    }
} 