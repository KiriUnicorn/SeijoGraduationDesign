package com.competition.backend.service;

import com.competition.backend.common.PageResult;
import com.competition.backend.entity.Competition;
import java.util.List;

public interface CompetitionService {
    /**
     * 获取热门竞赛列表
     * @param limit 限制数量
     * @return 竞赛列表
     */
    List<Competition> getHotCompetitions(Integer limit);
    
    /**
     * 分页获取所有竞赛
     * @param page 页码，从1开始
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<Competition> getAllCompetitions(Integer page, Integer pageSize);

    /**
     * 获取竞赛详情
     * @param competitionId 竞赛ID
     * @return 竞赛详情
     */
    Competition getCompetitionDetail(Integer competitionId);

    List<Competition> findByStatus(String status);
    
    Competition findById(Integer competitionId);
    
    Competition create(Competition competition);
    
    Competition update(Competition competition);
    
    void deleteById(Integer competitionId);

    /**
     * 获取当前用户的竞赛报名记录
     * @return 用户的竞赛报名记录列表
     */
    List<Competition> getUserRegistrations(Integer studentId);
} 