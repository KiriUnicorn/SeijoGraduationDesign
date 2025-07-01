package com.competition.backend.controller;

import com.competition.backend.common.Result;
import com.competition.backend.entity.Team;
import com.competition.backend.service.TeamService;
import com.competition.backend.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/teams")
public class TeamController {
    
    @Autowired
    private TeamService teamService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/competition/{competitionId}")
    public Result<List<Team>> getTeamsByCompetition(@PathVariable Integer competitionId) {
        log.info("获取竞赛队伍列表: competitionId={}", competitionId);
        try {
            List<Team> teams = teamService.getTeamsByCompetition(competitionId);
            return Result.success(teams);
        } catch (Exception e) {
            log.error("获取竞赛队伍列表失败", e);
            return Result.error("获取队伍列表失败");
        }
    }
    
    @PostMapping
    public Result<Team> createTeam(@RequestBody Team team, @RequestHeader("Authorization") String token) {
        log.info("创建队伍: team={}", team);
        try {
            // 从token中获取学生ID作为队长ID
            Integer studentId = jwtUtil.getUserIdFromToken(token);
            if (studentId == null) {
                return Result.error("未找到学生信息");
            }
            
            team.setCaptainId(studentId);
            team.setMemberCount(1); // 创建时只有队长一人
            
            Team createdTeam = teamService.createTeam(team);
            return Result.success(createdTeam);
        } catch (Exception e) {
            log.error("创建队伍失败", e);
            return Result.error("创建队伍失败");
        }
    }
    
    @DeleteMapping("/{teamId}")
    public Result<Void> deleteTeam(@PathVariable Integer teamId, @RequestHeader("Authorization") String token) {
        log.info("删除队伍: teamId={}", teamId);
        try {
            // 从token中获取学生ID
            Integer studentId = jwtUtil.getUserIdFromToken(token);
            if (studentId == null) {
                return Result.error("未找到学生信息");
            }
            
            // 检查是否是队长
            Team team = teamService.getTeamById(teamId);
            if (team == null) {
                return Result.error("队伍不存在");
            }
            
            if (!team.getCaptainId().equals(studentId)) {
                return Result.error("只有队长可以删除队伍");
            }
            
            teamService.deleteTeam(teamId);
            return Result.success();
        } catch (Exception e) {
            log.error("删除队伍失败", e);
            return Result.error("删除队伍失败");
        }
    }
} 