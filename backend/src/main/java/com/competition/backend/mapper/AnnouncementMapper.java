package com.competition.backend.mapper;

import com.competition.backend.entity.Announcement;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AnnouncementMapper {
    /**
     * 获取最新公告列表
     * @param limit 限制数量
     * @return 公告列表
     */
    @Select("SELECT * FROM announcement ORDER BY created_at DESC LIMIT #{limit}")
    List<Announcement> selectLatestAnnouncements(Integer limit);
    
    /**
     * 获取公告总数
     * @return 公告总数
     */
    @Select("SELECT COUNT(*) FROM announcement")
    Long countAnnouncements();
    
    /**
     * 分页获取所有公告
     * @param offset 偏移量
     * @param pageSize 每页大小
     * @return 公告列表
     */
    @Select("SELECT * FROM announcement ORDER BY created_at DESC LIMIT #{offset}, #{pageSize}")
    List<Announcement> selectAllAnnouncements(Integer offset, Integer pageSize);
    
    /**
     * 根据ID获取公告详情
     * @param id 公告ID
     * @return 公告详情
     */
    @Select("SELECT a.*, admin.name as adminName FROM announcement a " +
            "LEFT JOIN admin ON a.admin_id = admin.admin_id " +
            "WHERE a.announcement_id = #{id}")
    Announcement selectAnnouncementById(Long id);

    @Select("SELECT * FROM announcement ORDER BY created_at DESC")
    List<Announcement> findAll();

    @Insert("INSERT INTO announcement (admin_id, title, content, created_at) VALUES (#{adminId}, #{title}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "announcementId")
    int insert(Announcement announcement);

    @Update("UPDATE announcement SET title = #{title}, content = #{content} WHERE announcement_id = #{announcementId}")
    int update(Announcement announcement);

    @Delete("DELETE FROM announcement WHERE announcement_id = #{id}")
    int deleteById(Integer id);
} 