package com.competition.backend.controller;

import com.competition.backend.common.Result;
import com.competition.backend.entity.CompetitionLevel;
import com.competition.backend.entity.Department;
import com.competition.backend.service.CompetitionLevelService;
import com.competition.backend.service.DepartmentService;
import com.competition.backend.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/competition-levels")
@CrossOrigin
public class CompetitionLevelController {
    @Autowired
    private CompetitionLevelService competitionLevelService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/findAll")
    public Result<List<CompetitionLevel>> findAllLevels(){
        return Result.success(competitionLevelService.findAll());
    }

}
