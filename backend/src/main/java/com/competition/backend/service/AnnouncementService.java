package com.competition.backend.service;

import com.competition.backend.common.PageResult;
import com.competition.backend.entity.Announcement;
import java.util.List;

public interface AnnouncementService {
    /**
     * 获取最新公告列表
     * @param limit 限制数量
     * @return 公告列表
     */
    List<Announcement> getLatestAnnouncements(Integer limit);
    
    /**
     * 分页获取所有公告
     * @param page 页码，从1开始
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<Announcement> getAllAnnouncements(Integer page, Integer pageSize);
    
    /**
     * 根据ID获取公告详情
     * @param id 公告ID
     * @return 公告详情
     */
    Announcement getAnnouncementById(Long id);

    List<Announcement> findAllAnnouncements();
    Announcement createAnnouncement(Announcement announcement);
    Announcement updateAnnouncement(Announcement announcement);
    void deleteAnnouncement(Integer id);
} 