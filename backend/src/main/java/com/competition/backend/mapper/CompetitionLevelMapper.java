package com.competition.backend.mapper;
import com.competition.backend.entity.CompetitionLevel;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface CompetitionLevelMapper {

    @Select("SELECT * FROM competition_level order by level_id")
    List<CompetitionLevel> findAll();
}
