package com.competition.backend.controller;

import com.competition.backend.common.PageResult;
import com.competition.backend.common.Result;
import com.competition.backend.entity.Announcement;
import com.competition.backend.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    private final AnnouncementService announcementService;

    /**
     * 获取最新公告列表
     * @param limit 限制数量
     * @return 公告列表
     */
    @GetMapping("/latest")
    public Result<List<Announcement>> getLatestAnnouncements(
            @RequestParam(defaultValue = "3") Integer limit) {
        log.info("获取最新公告列表，限制数量：{}", limit);
        try {
            List<Announcement> announcements = announcementService.getLatestAnnouncements(limit);
            return Result.success(announcements);
        } catch (Exception e) {
            log.error("获取最新公告列表失败：{}", e.getMessage(), e);
            return Result.error("获取最新公告列表失败");
        }
    }

    /**
     * 分页获取所有公告
     * @param page 页码，从1开始
     * @param pageSize 每页大小
     * @return 公告列表和总数
     */
    @GetMapping("/findAll")
    public Result<PageResult<Announcement>> getAllAnnouncements(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(announcementService.getAllAnnouncements(page, pageSize));
    }

    /**
     * 获取公告详情
     * @param id 公告ID
     * @return 公告详情
     */
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncementDetail(@PathVariable Long id) {
        log.info("获取公告详情，ID：{}", id);
        try {
            Announcement announcement = announcementService.getAnnouncementById(id);
            if (announcement == null) {
                return Result.error("公告不存在");
            }
            return Result.success(announcement);
        } catch (Exception e) {
            log.error("获取公告详情失败：{}", e.getMessage(), e);
            return Result.error("获取公告详情失败");
        }
    }

    @GetMapping
    public Result<List<Announcement>> getAllAnnouncements() {
        log.info("获取所有公告信息");
        try {
            List<Announcement> announcements = announcementService.findAllAnnouncements();
            return Result.success(announcements);
        } catch (Exception e) {
            log.error("获取公告列表失败", e);
            return Result.error("获取公告列表失败");
        }
    }

    @PostMapping
    public Result<Announcement> createAnnouncement(@RequestBody Announcement announcement) {
        log.info("创建公告: {}", announcement);
        try {
            Announcement created = announcementService.createAnnouncement(announcement);
            return Result.success(created);
        } catch (Exception e) {
            log.error("创建公告失败", e);
            return Result.error("创建公告失败");
        }
    }

    @PutMapping("/{id}")
    public Result<Announcement> updateAnnouncement(@PathVariable Integer id, @RequestBody Announcement announcement) {
        log.info("更新公告: id={}, announcement={}", id, announcement);
        try {
            announcement.setAnnouncementId(id);
            Announcement updated = announcementService.updateAnnouncement(announcement);
            return Result.success(updated);
        } catch (Exception e) {
            log.error("更新公告失败", e);
            return Result.error("更新公告失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAnnouncement(@PathVariable Integer id) {
        log.info("删除公告: id={}", id);
        try {
            announcementService.deleteAnnouncement(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除公告失败", e);
            return Result.error("删除公告失败");
        }
    }
} 