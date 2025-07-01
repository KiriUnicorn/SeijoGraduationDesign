package com.competition.backend.mapper;

import com.competition.backend.entity.Competition;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CompetitionMapper {
    /**
     * 获取热门竞赛列表
     * 根据报名人数和创建时间排序
     * @param limit 限制数量
     * @return 竞赛列表
     */
    @Select("SELECT c.*, COUNT(r.registration_id) as registration_count " +
            "FROM competition c " +
            "LEFT JOIN registration r ON c.competition_id = r.competition_id " +
            "WHERE c.status = 'pending' " +
            "GROUP BY c.competition_id " +
            "ORDER BY registration_count DESC, c.created_at DESC " +
            "LIMIT #{limit}")
    List<Competition> selectHotCompetitions(Integer limit);
    
    /**
     * 获取竞赛总数
     * @return 竞赛总数
     */
    @Select("SELECT COUNT(*) FROM competition WHERE status = 1")
    Long countCompetitions();
    
    /**
     * 分页获取所有竞赛
     * @param offset 偏移量
     * @param pageSize 每页大小
     * @return 竞赛列表
     */
    @Select("SELECT * FROM competition WHERE status like '%' ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Competition> selectAllCompetitions(Integer offset, Integer pageSize);

    @Select({
            "<script>",
            "SELECT c.*, ",
            "hu.name as host_unit_name, ",
            "d.name as department_name, ",
            "cl.level_name as level_name, ",
            "a.name as admin_creator_name, ",
            "t.name as teacher_creator_name, ",
            "(SELECT COUNT(*) FROM registration r WHERE r.competition_id = c.competition_id) as current_participants ",
            "FROM competition c ",
            "LEFT JOIN host_unit hu ON c.host_unit_id = hu.host_unit_id ",
            "LEFT JOIN department d ON c.responsible_department_id = d.department_id ",
            "LEFT JOIN competition_level cl ON c.level_id = cl.level_id ",
            "LEFT JOIN admin a ON c.admin_creator_id = a.admin_id ",
            "LEFT JOIN teacher t ON c.teacher_creator_id = t.teacher_id ",
            "<where>",
            "   <if test='status != null and status != \"\"'>",
            "       AND c.status = #{status}",
            "   </if>",
            "</where>",
            "ORDER BY c.created_at DESC",
            "</script>"
    })
    @Results({
            @Result(property = "competitionId", column = "competition_id"),
            @Result(property = "title", column = "title"),
            @Result(property = "description", column = "description"),
            @Result(property = "hostUnitId", column = "host_unit_id"),
            @Result(property = "hostUnitName", column = "host_unit_name"),
            @Result(property = "responsibleDepartmentId", column = "responsible_department_id"),
            @Result(property = "departmentName", column = "department_name"),
            @Result(property = "levelId", column = "level_id"),
            @Result(property = "levelName", column = "level_name"),
            @Result(property = "signupStartTime", column = "signup_start_time"),
            @Result(property = "signupEndTime", column = "signup_end_time"),
            @Result(property = "competitionStartTime", column = "competition_start_time"),
            @Result(property = "competitionEndTime", column = "competition_end_time"),
            @Result(property = "status", column = "status"),
            @Result(property = "adminCreatorId", column = "admin_creator_id"),
            @Result(property = "adminCreatorName", column = "admin_creator_name"),
            @Result(property = "teacherCreatorId", column = "teacher_creator_id"),
            @Result(property = "teacherCreatorName", column = "teacher_creator_name"),
            @Result(property = "currentParticipants", column = "current_participants"),
            @Result(property = "createdAt", column = "created_at")
    })
    List<Competition> findByStatus(@Param("status") String status);

    @Select("SELECT c.*, " +
            "hu.name as host_unit_name, " +
            "d.name as department_name, " +
            "cl.level_name as level_name, " +
            "a.name as admin_creator_name, " +
            "t.name as teacher_creator_name, " +
            "(SELECT COUNT(*) FROM registration r WHERE r.competition_id = c.competition_id) as current_participants " +
            "FROM competition c " +
            "LEFT JOIN host_unit hu ON c.host_unit_id = hu.host_unit_id " +
            "LEFT JOIN department d ON c.responsible_department_id = d.department_id " +
            "LEFT JOIN competition_level cl ON c.level_id = cl.level_id " +
            "LEFT JOIN admin a ON c.admin_creator_id = a.admin_id " +
            "LEFT JOIN teacher t ON c.teacher_creator_id = t.teacher_id " +
            "WHERE c.competition_id = #{competitionId}")
    @Results({
        @Result(property = "competitionId", column = "competition_id"),
        @Result(property = "title", column = "title"),
        @Result(property = "description", column = "description"),
        @Result(property = "hostUnitId", column = "host_unit_id"),
        @Result(property = "hostUnitName", column = "host_unit_name"),
        @Result(property = "responsibleDepartmentId", column = "responsible_department_id"),
        @Result(property = "departmentName", column = "department_name"),
        @Result(property = "levelId", column = "level_id"),
        @Result(property = "levelName", column = "level_name"),
        @Result(property = "signupStartTime", column = "signup_start_time"),
        @Result(property = "signupEndTime", column = "signup_end_time"),
        @Result(property = "competitionStartTime", column = "competition_start_time"),
        @Result(property = "competitionEndTime", column = "competition_end_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "adminCreatorId", column = "admin_creator_id"),
        @Result(property = "adminCreatorName", column = "admin_creator_name"),
        @Result(property = "teacherCreatorId", column = "teacher_creator_id"),
        @Result(property = "teacherCreatorName", column = "teacher_creator_name"),
        @Result(property = "currentParticipants", column = "current_participants"),
        @Result(property = "createdAt", column = "created_at")
    })
    Competition findById(Integer competitionId);

    @Insert("INSERT INTO competition (title, description, host_unit_id, responsible_department_id, " +
            "level_id, signup_start_time, signup_end_time, competition_start_time, competition_end_time, " +
            "admin_creator_id, teacher_creator_id) " +
            "VALUES (#{title}, #{description}, #{hostUnitId}, #{responsibleDepartmentId}, " +
            "#{levelId}, #{signupStartTime}, #{signupEndTime}, #{competitionStartTime}, #{competitionEndTime}, " +
            "#{adminCreatorId}, #{teacherCreatorId})")
    @Options(useGeneratedKeys = true, keyProperty = "competitionId")
    int insert(Competition competition);

    @Update("UPDATE competition SET " +
            "title = #{title}, " +
            "description = #{description}, " +
            "host_unit_id = #{hostUnitId}, " +
            "responsible_department_id = #{responsibleDepartmentId}, " +
            "level_id = #{levelId}, " +
            "signup_start_time = #{signupStartTime}, " +
            "signup_end_time = #{signupEndTime}, " +
            "competition_start_time = #{competitionStartTime}, " +
            "competition_end_time = #{competitionEndTime} " +
            "WHERE competition_id = #{competitionId}")
    int update(Competition competition);

    @Delete("DELETE FROM competition WHERE competition_id = #{competitionId}")
    int deleteById(Integer competitionId);

    /**
     * 查询用户的竞赛报名记录
     * @param userId 用户ID
     * @return 竞赛报名记录列表
     */
    @Select({
            "SELECT",
            "   c.title AS competition_title,",
            "   r.status AS registration_status,",
            "   t.name AS team_name,",
            "   te.name AS teacher_name",
            "FROM",
            "   competition c",
            "INNER JOIN registration r ON c.competition_id = r.competition_id",
            "LEFT JOIN team t ON r.team_id = t.team_id",
            "LEFT JOIN teacher te ON r.teacher_approver_id = te.teacher_id",
            "WHERE",
            "   r.student_id = #{userId}",
            "ORDER BY c.competition_start_time DESC"
    })
    @Results({
            @Result(property = "competitionTitle", column = "competition_title"),
            @Result(property = "registrationStatus", column = "registration_status"),
            @Result(property = "teamName", column = "team_name"),
            @Result(property = "teacherName", column = "teacher_name")
    })
    List<Competition> findUserRegistrations(Integer userId);
} 