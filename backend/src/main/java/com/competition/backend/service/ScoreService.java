package com.competition.backend.service;

import com.competition.backend.entity.Score;
import com.competition.backend.entity.TeamMember;
import com.competition.backend.mapper.ScoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScoreService {
    
    private static final Logger logger = LoggerFactory.getLogger(ScoreService.class);
    
    @Autowired
    private ScoreMapper scoreMapper;

    public Score getScoreDetail(Integer scoreId) {
        logger.info("Getting score detail for scoreId: {}", scoreId);

        try {
            // 1. 获取基础分数信息
            Score score = scoreMapper.findById(scoreId);

            if (score != null) {
                logger.info("Found score: {}", score);

                // 2. 获取团队成员完整信息
                List<TeamMember> teamMembers = scoreMapper.findTeamMembersByScoreId(scoreId);

                // 3. 设置成员信息到Score对象
                score.setMembers(teamMembers);

                logger.info("Score details - teamName: {}, competitionName: {}, captainName: {}, members: {}",
                        score.getTeamName(),
                        score.getCompetitionName(),
                        score.getCaptainName(),
                        teamMembers.stream().map(TeamMember::getStudentName).collect(Collectors.toList()));
            }

            return score;
        } catch (Exception e) {
            logger.error("Error getting score detail for scoreId: {}", scoreId, e);
            throw new RuntimeException("Failed to get score details", e);
        }
    }

    public List<Score> findScoresByStudentId(Integer studentId) {
        logger.info("Getting scores for studentId: {}", studentId);

        try {
            return scoreMapper.findScoresByStudentId(studentId);
        } catch (Exception e) {
            logger.error("Error getting scores for studentId: {}", studentId, e);
            throw new RuntimeException("Failed to get scores", e);
        }
    }
} 
 