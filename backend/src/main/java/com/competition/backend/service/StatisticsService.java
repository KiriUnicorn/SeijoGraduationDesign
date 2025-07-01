package com.competition.backend.service;

import java.util.List;
import java.util.Map;

public interface StatisticsService {
    List<Map<String, Object>> getDepartmentRegistrationStats();

    List<Map<String, Object>> getGradeRegistrationStats();
} 
