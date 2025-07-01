package com.competition.backend.controller;

import com.competition.backend.entity.Score;
import com.competition.backend.entity.Team;
import com.competition.backend.mapper.ScoreMapper;
import com.competition.backend.service.ScoreService;
import com.competition.backend.util.JwtUtil;
import com.competition.backend.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/scores")
@RequiredArgsConstructor
public class ScoreController {
    
    private final ScoreService scoreService;

    @Autowired
    private ScoreMapper scoreMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/student/{studentId}")
    public Result<List<Score>> getStudentScores(@PathVariable Integer studentId) {
        try {
            List<Score> scores = scoreService.findScoresByStudentId(studentId);
            log.info("获取学生成绩列表成功: studentId={}, 共{}条记录", studentId, scores.size());
            return Result.success(scores);
        } catch (Exception e) {
            log.error("获取学生成绩列表失败", e);
            return Result.error("获取成绩列表失败：" + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<Score> getScoreDetail(@PathVariable Integer id) {
        try {
            Score score = scoreService.getScoreDetail(id);
            log.info("获取成绩详情成功: id={}", id);
            return Result.success(score);
        } catch (Exception e) {
            log.error("获取成绩详情失败", e);
            return Result.error("获取成绩详情失败：" + e.getMessage());
        }
    }

    //教师获取成绩列表
    @GetMapping("/teacher")
    public Result<List<Score>> getTeacherScores(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Integer teacherId = jwtUtil.getUserIdFromToken(token);
            List<Score> scores = scoreMapper.findByTeacherId(teacherId);
            return Result.success(scores);
        } catch (Exception e) {
            return Result.error("获取成绩列表失败：" + e.getMessage());
        }
    }
    //教师获取未录入成绩的队伍列表
    @GetMapping("/teacher/teams")
    public Result<List<Team>> getTeacherTeamsWithoutScore(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Integer teacherId = jwtUtil.getUserIdFromToken(token);
            List<Team> teams = scoreMapper.findTeamsWithoutScore(teacherId);
            return Result.success(teams);
        } catch (Exception e) {
            return Result.error("获取未录入成绩的队伍列表失败：" + e.getMessage());
        }
    }
    //教师录入成绩
    @PostMapping
    public Result<?> addScore(@RequestBody Score score, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Integer teacherId = jwtUtil.getUserIdFromToken(token);
            score.setTeacherId(teacherId);
            
            // 检查是否已经存在成绩记录
            Score existingScore = scoreMapper.findByTeamId(score.getTeamId());
            if (existingScore != null) {
                return Result.error("该队伍已有成绩记录");
            }
            
            scoreMapper.insert(score);
            return Result.success();
        } catch (Exception e) {
            System.out.println(Result.error("添加成绩失败：" + e.getMessage()));
            return Result.error("添加成绩失败：" + e.getMessage());
        }
    }
    //教师更新成绩
    @PutMapping("/{scoreId}")
    public Result<?> updateScore(@PathVariable Integer scoreId, @RequestBody Score score, HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Integer teacherId = jwtUtil.getUserIdFromToken(token);
            score.setTeacherId(teacherId);
            score.setScoreId(scoreId);
            
            scoreMapper.update(score);
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新成绩失败：" + e.getMessage());
        }
    }
} 
 