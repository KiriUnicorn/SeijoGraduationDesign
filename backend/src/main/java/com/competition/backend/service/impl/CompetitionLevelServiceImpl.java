package com.competition.backend.service.impl;

import com.competition.backend.entity.CompetitionLevel;
import com.competition.backend.mapper.CompetitionLevelMapper;
import com.competition.backend.service.CompetitionLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CompetitionLevelServiceImpl implements CompetitionLevelService {

    private final CompetitionLevelMapper competitionLevelMapper;
    @Override
    public List<CompetitionLevel> findAll() {
        return competitionLevelMapper.findAll();
    }
}
