package com.competition.backend.service;

import com.competition.backend.entity.Team;
import java.util.List;

public interface TeamService {
    List<Team> getTeamsByCompetition(Integer competitionId);
    Team createTeam(Team team);
    Team getTeamById(Integer teamId);
    void deleteTeam(Integer teamId);
} 
 
 