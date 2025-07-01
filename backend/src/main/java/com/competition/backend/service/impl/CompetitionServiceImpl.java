package com.competition.backend.service.impl;

import com.competition.backend.common.PageResult;
import com.competition.backend.entity.Competition;
import com.competition.backend.entity.User;
import com.competition.backend.mapper.CompetitionMapper;
import com.competition.backend.mapper.RegistrationMapper;
import com.competition.backend.mapper.UserMapper;
import com.competition.backend.service.CompetitionService;
import com.competition.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {
    private static final Logger logger = LoggerFactory.getLogger(CompetitionServiceImpl.class);

    private final CompetitionMapper competitionMapper;
    private final UserMapper userMapper;
    private final RegistrationMapper registrationMapper;

    @Autowired
    private JwtUtil jwtUtil;
    @Override
    public List<Competition> getHotCompetitions(Integer limit) {
        return competitionMapper.selectHotCompetitions(limit);
    }

    @Override
    public PageResult<Competition> getAllCompetitions(Integer page, Integer pageSize) {
        // 计算偏移量
        int offset = (page - 1) * pageSize;
        // 获取总记录数
        Long total = competitionMapper.countCompetitions();
        // 获取当前页数据
        List<Competition> list = competitionMapper.selectAllCompetitions(offset, pageSize);
        // 返回分页结果
        return new PageResult<>(list, total, page, pageSize);
    }

    @Override
    public Competition getCompetitionDetail(Integer competitionId) {
        logger.info("Getting competition detail for ID: {}", competitionId);
        Competition competition = competitionMapper.findById(competitionId);
        if (competition == null) {
            logger.warn("Competition not found with ID: {}", competitionId);
            return null;
        }
        logger.info("Found competition: {}", competition.getTitle());
        return competition;
    }

    @Override
    public List<Competition> findByStatus(String status) {
        logger.info("Finding competitions by status: {}", status);
        List<Competition> competitions = competitionMapper.findByStatus(status);
        logger.info("Found {} competitions", competitions.size());
        return competitions;
    }

    @Override
    public Competition findById(Integer competitionId) {
        logger.info("Finding competition by ID: {}", competitionId);
        Competition competition = competitionMapper.findById(competitionId);
        if (competition == null) {
            logger.warn("Competition not found with ID: {}", competitionId);
        } else {
            logger.info("Found competition: {}", competition.getTitle());
        }
        return competition;
    }

    @Override
    @Transactional
    public Competition create(Competition competition) {
        logger.info("Creating new competition: {}", competition.getTitle());
        try {
            competitionMapper.insert(competition);
            logger.info("Successfully created competition with ID: {}", competition.getCompetitionId());
            return competition;
        } catch (Exception e) {
            logger.error("Error creating competition", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public Competition update(Competition competition) {
        logger.info("Updating competition with ID: {}", competition.getCompetitionId());
        try {
            Competition existingCompetition = competitionMapper.findById(competition.getCompetitionId());
            if (existingCompetition == null) {
                logger.warn("Competition not found with ID: {}", competition.getCompetitionId());
                throw new RuntimeException("Competition not found");
            }

            competitionMapper.update(competition);
            logger.info("Successfully updated competition: {}", competition.getTitle());
            return competition;
        } catch (Exception e) {
            logger.error("Error updating competition", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void deleteById(Integer competitionId) {
        logger.info("Deleting competition with ID: {}", competitionId);
        try {
            Competition existingCompetition = competitionMapper.findById(competitionId);
            if (existingCompetition == null) {
                logger.warn("Competition not found with ID: {}", competitionId);
                throw new RuntimeException("Competition not found");
            }

            competitionMapper.deleteById(competitionId);
            logger.info("Successfully deleted competition with ID: {}", competitionId);
        } catch (Exception e) {
            logger.error("Error deleting competition", e);
            throw e;
        }
    }

    @Override
    public List<Competition> getUserRegistrations(Integer studentId) {
        // 获取当前登录用户ID

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("用户未登录");
        }
        
/*        String username = authentication.getName();
        System.out.println("1111111111111111111111"+username);*/
        User user = userMapper.findByStudentId(studentId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 获取用户的竞赛报名记录
        return competitionMapper.findUserRegistrations(user.getStudentId());
    }
} 