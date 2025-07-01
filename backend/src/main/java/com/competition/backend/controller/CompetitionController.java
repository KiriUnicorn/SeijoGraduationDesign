package com.competition.backend.controller;

import com.competition.backend.common.PageResult;
import com.competition.backend.common.Result;
import com.competition.backend.entity.Announcement;
import com.competition.backend.entity.Competition;
import com.competition.backend.service.CompetitionService;
import com.competition.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/competitions")
@RequiredArgsConstructor
@CrossOrigin
public class CompetitionController {
    private final CompetitionService competitionService;

    @Autowired
    private JwtUtil jwtUtil;
    /**
     * 获取热门竞赛列表
     * @param limit 限制数量
     * @return 竞赛列表
     */
    @GetMapping("/hot")
    public Result<List<Competition>> getHotCompetitions(
            @RequestParam(defaultValue = "3") Integer limit) {
        log.info("获取热门竞赛列表，限制数量：{}", limit);
        try {
            List<Competition> competitions = competitionService.getHotCompetitions(limit);
            return Result.success(competitions);
        } catch (Exception e) {
            log.error("获取热门竞赛列表失败：{}", e.getMessage(), e);
            return Result.error("获取热门竞赛列表失败");
        }
    }

    @GetMapping("/findAll")
    public Result<PageResult<Competition>> getAllCompetitions(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(competitionService.getAllCompetitions(page, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Competition> getCompetitionDetail(@PathVariable Integer id) {
        log.info("获取竞赛详情，ID：{}", id);
        try {
            Competition competition = competitionService.getCompetitionDetail(id);
            if (competition != null) {
                return Result.success(competition);
            }
            return Result.error("竞赛不存在");
        } catch (Exception e) {
            log.error("获取竞赛详情失败：{}", e.getMessage(), e);
            return Result.error("获取竞赛详情失败");
        }
    }

    @GetMapping
    public Result<List<Competition>> list(@RequestParam(required = false) String status) {
        log.info("Listing competitions with status: {}", status);
        try {
            List<Competition> competitions = competitionService.findByStatus(status);
            log.info("Found {} competitions", competitions.size());
            return Result.success(competitions);
        } catch (Exception e) {
            log.error("Error listing competitions", e);
            return Result.error("获取竞赛列表失败：" + e.getMessage());
        }
    }

    @PostMapping
    public Result<Competition> create(@RequestBody Competition competition) {
        log.info("Creating new competition: {}", competition.getTitle());
        try {
            Competition createdCompetition = competitionService.create(competition);
            log.info("Successfully created competition with ID: {}", createdCompetition.getCompetitionId());
            return Result.success(createdCompetition);
        } catch (Exception e) {
            log.error("Error creating competition", e);
            return Result.error("创建竞赛失败：" + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Competition> update(@PathVariable("id") Integer id, @RequestBody Competition competition) {
        log.info("Updating competition with ID: {}", id);
        try {
            competition.setCompetitionId(id);
            Competition updatedCompetition = competitionService.update(competition);
            log.info("Successfully updated competition: {}", updatedCompetition.getTitle());
            return Result.success(updatedCompetition);
        } catch (Exception e) {
            log.error("Error updating competition", e);
            return Result.error("更新竞赛失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Integer id) {
        log.info("Deleting competition with ID: {}", id);
        try {
            competitionService.deleteById(id);
            log.info("Successfully deleted competition with ID: {}", id);
            return Result.success();
        } catch (Exception e) {
            log.error("Error deleting competition", e);
            return Result.error("删除竞赛失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户的竞赛报名记录
     * @return 用户的竞赛报名记录列表
     */
    @GetMapping("/user/registrations")
    public Result<List<Competition>> getUserRegistrations(@RequestHeader("Authorization") String authHeader) {

        log.info("获取当前用户的竞赛报名记录");
        try {
            // 去除 "Bearer " 前缀
            String token = authHeader.replace("Bearer ", "");
            Integer studentId = jwtUtil.getUserIdFromToken(token);
            System.out.println("当前学生id="+ studentId);
            List<Competition> registrations = competitionService.getUserRegistrations(studentId);
            return Result.success(registrations);
        } catch (Exception e) {
            log.error("获取用户竞赛报名记录失败：{}", e.getMessage(), e);
            return Result.error("获取报名记录失败：" + e.getMessage());
        }
    }
} 