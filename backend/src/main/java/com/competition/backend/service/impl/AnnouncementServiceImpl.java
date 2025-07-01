package com.competition.backend.service.impl;

import com.competition.backend.common.PageResult;
import com.competition.backend.entity.Announcement;
import com.competition.backend.mapper.AnnouncementMapper;
import com.competition.backend.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
    private final AnnouncementMapper announcementMapper;

    @Override
    public List<Announcement> getLatestAnnouncements(Integer limit) {
        return announcementMapper.selectLatestAnnouncements(limit);
    }

    @Override
    public PageResult<Announcement> getAllAnnouncements(Integer page, Integer pageSize) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        // 获取总记录数
        Long total = announcementMapper.countAnnouncements();
        // 获取当前页数据
        List<Announcement> list = announcementMapper.selectAllAnnouncements(offset, pageSize);
        // 返回分页结果
        return new PageResult<>(list, total, page, pageSize);
    }

    @Override
    public Announcement getAnnouncementById(Long id) {
        return announcementMapper.selectAnnouncementById(id);
    }

    @Override
    public List<Announcement> findAllAnnouncements() {
        return announcementMapper.findAll();
    }

    @Override
    @Transactional
    public Announcement createAnnouncement(Announcement announcement) {
        announcementMapper.insert(announcement);
        return announcement;
    }

    @Override
    @Transactional
    public Announcement updateAnnouncement(Announcement announcement) {
        int rows = announcementMapper.update(announcement);
        if (rows == 0) {
            throw new RuntimeException("公告不存在或更新失败");
        }
        return announcement;
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Integer id) {
        int rows = announcementMapper.deleteById(id);
        if (rows == 0) {
            throw new RuntimeException("公告不存在或删除失败");
        }
    }
} 