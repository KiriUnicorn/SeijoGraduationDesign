package com.competition.backend.service.impl;

import com.competition.backend.mapper.RegistrationMapper;
import com.competition.backend.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private RegistrationMapper registrationMapper;

    @Override
    public List<Map<String, Object>> getDepartmentRegistrationStats() {
        return registrationMapper.getDepartmentRegistrationStats();
    }

    @Override
    public List<Map<String, Object>> getGradeRegistrationStats() {
        return registrationMapper.getGradeRegistrationStats();
    }
} 
