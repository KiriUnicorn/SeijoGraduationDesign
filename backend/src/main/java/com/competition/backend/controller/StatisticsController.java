package com.competition.backend.controller;

import com.competition.backend.common.Result;
import com.competition.backend.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/department-registration")
    public Result<List<Map<String, Object>>> getDepartmentRegistrationStats() {
        List<Map<String, Object>> stats = statisticsService.getDepartmentRegistrationStats();
        return Result.success(stats);
    }

    @GetMapping("/grade-registration")
    public Result<List<Map<String,Object>>> getGradeRegistrationStats(){
        List<Map<String, Object>> stats = statisticsService.getGradeRegistrationStats();
        return Result.success(stats);
    }
} 
