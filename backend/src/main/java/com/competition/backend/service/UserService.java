package com.competition.backend.service;

import com.competition.backend.entity.User;
import java.util.List;

public interface UserService {
    /**
     * 修改密码
     * @param token 用户token
     * @param oldPassword 原密码
     * @param newPassword 新密码
     */
    void changePassword(String token, String oldPassword, String newPassword);

    List<User> findAllUsers();

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    User updateUser(User user);

    /**
     * 删除用户
     * @param id 用户ID
     * @param role 用户角色
     */
    void deleteUser(Integer id, String role);

    User createUser(User user);
} 