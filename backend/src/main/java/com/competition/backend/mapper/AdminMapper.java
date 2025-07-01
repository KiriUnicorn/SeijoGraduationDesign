package com.competition.backend.mapper;

import com.competition.backend.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdminMapper {
    /**
     * 根据邮箱查询管理员
     * @param email 邮箱
     * @return 管理员信息
     */
    @Select("SELECT * FROM admin WHERE email = #{email}")
    Admin findByEmail(String email);

    @Select("SELECT * FROM admin WHERE admin_id = #{adminId}")
    Admin selectById(Integer adminId);

    @Update("UPDATE admin SET password = #{password} WHERE admin_id = #{adminId}")
    void updatePassword(Integer adminId, String password);
} 