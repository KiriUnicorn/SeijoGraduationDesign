package com.competition.backend.mapper;

import com.competition.backend.entity.HostUnit;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface HostUnitMapper {
    @Select("SELECT * FROM host_unit")
    List<HostUnit> findAll();

    @Insert("INSERT INTO host_unit (name) VALUES (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "hostUnitId")
    int insert(HostUnit hostUnit);

    @Update("UPDATE host_unit SET name = #{name} WHERE host_unit_id = #{hostUnitId}")
    int update(HostUnit hostUnit);

    @Delete("DELETE FROM host_unit WHERE host_unit_id = #{id}")
    int deleteById(Integer id);
}
