package com.competition.backend.service;

import com.competition.backend.entity.HostUnit;
import org.springframework.stereotype.Service;

import java.util.List;

public interface HostUnitService {
    List<HostUnit> findAllHostUnits();
    HostUnit createHostUnit(HostUnit hostUnit);
    HostUnit updateHostUnit(HostUnit hostUnit);
    void deleteHostUnit(Integer id);
}
