package com.competition.backend.controller;

import com.competition.backend.entity.HostUnit;
import com.competition.backend.service.HostUnitService;
import com.competition.backend.util.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/host-units")
@RequiredArgsConstructor
public class HostUnitController {
    private final HostUnitService hostUnitService;

    @GetMapping
    public Result<List<HostUnit>> getAllHostUnits() {
        log.info("获取所有主办单位信息");
        try {
            List<HostUnit> hostUnits = hostUnitService.findAllHostUnits();
            return Result.success(hostUnits);
        } catch (Exception e) {
            log.error("获取主办单位列表失败", e);
            return Result.error("获取主办单位列表失败");
        }
    }

    @PostMapping
    public Result<HostUnit> createHostUnit(@RequestBody HostUnit hostUnit) {
        log.info("创建主办单位: {}", hostUnit);
        try {
            HostUnit created = hostUnitService.createHostUnit(hostUnit);
            return Result.success(created);
        } catch (Exception e) {
            log.error("创建主办单位失败", e);
            return Result.error("创建主办单位失败");
        }
    }

    @PutMapping("/{id}")
    public Result<HostUnit> updateHostUnit(@PathVariable Integer id, @RequestBody HostUnit hostUnit) {
        log.info("更新主办单位: id={}, hostUnit={}", id, hostUnit);
        try {
            hostUnit.setHostUnitId(id);
            HostUnit updated = hostUnitService.updateHostUnit(hostUnit);
            return Result.success(updated);
        } catch (Exception e) {
            log.error("更新主办单位失败", e);
            return Result.error("更新主办单位失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteHostUnit(@PathVariable Integer id) {
        log.info("删除主办单位: id={}", id);
        try {
            hostUnitService.deleteHostUnit(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除主办单位失败", e);
            return Result.error("删除主办单位失败");
        }
    }
}

