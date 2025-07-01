package com.competition.backend.service.impl;

import com.competition.backend.entity.HostUnit;
import com.competition.backend.mapper.HostUnitMapper;
import com.competition.backend.service.HostUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HostUnitServiceImpl implements HostUnitService {
    private final HostUnitMapper hostUnitMapper;

    @Override
    public List<HostUnit> findAllHostUnits() {
        return hostUnitMapper.findAll();
    }

    @Override
    @Transactional
    public HostUnit createHostUnit(HostUnit hostUnit) {
        hostUnitMapper.insert(hostUnit);
        return hostUnit;
    }

    @Override
    @Transactional
    public HostUnit updateHostUnit(HostUnit hostUnit) {
        int rows = hostUnitMapper.update(hostUnit);
        if (rows == 0) {
            throw new RuntimeException("主办单位不存在或更新失败");
        }
        return hostUnit;
    }

    @Override
    @Transactional
    public void deleteHostUnit(Integer id) {
        int rows = hostUnitMapper.deleteById(id);
        if (rows == 0) {
            throw new RuntimeException("主办单位不存在或删除失败");
        }
    }
}
