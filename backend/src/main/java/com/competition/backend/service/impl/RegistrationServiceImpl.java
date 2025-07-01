package com.competition.backend.service.impl;

import com.competition.backend.entity.Registration;
import com.competition.backend.entity.Team;
import com.competition.backend.mapper.RegistrationMapper;
import com.competition.backend.mapper.TeamMapper;
import com.competition.backend.mapper.TeamMemberMapper;
import com.competition.backend.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RegistrationServiceImpl implements RegistrationService {
    
    @Autowired
    private RegistrationMapper registrationMapper;
    
    @Autowired
    private TeamMapper teamMapper;
    
    @Autowired
    private TeamMemberMapper teamMemberMapper;
    
    @Override
    @Transactional
    public Registration createRegistration(Registration registration) {
        // 获取队伍信息
        Team team = teamMapper.selectById(registration.getTeamId());
        if (team != null) {
            registration.setTeacherApproverId(Integer.parseInt(team.getTeacherId()));
        }
        
        registrationMapper.insert(registration);
        return registration;
    }
    
    @Override
    public Registration getRegistrationById(Integer registrationId) {
        return registrationMapper.selectById(registrationId);
    }
    
    @Override
    @Transactional
    public Registration updateRegistration(Registration registration) {
        if ("approved".equals(registration.getStatus())) {
            // 如果审核通过，将学生添加到队伍成员表中
            registrationMapper.insertTeamMember(registration.getStudentId(), registration.getTeamId());
            
            // 更新队伍成员数量
            Team team = teamMapper.selectById(registration.getTeamId());
            if (team != null) {
                team.setMemberCount(team.getMemberCount() + 1);
                teamMapper.updateMemberCount(team);
            }
        } else if ("rejected".equals(registration.getStatus())) {
            // 如果是创建队伍的申请被拒绝，删除队伍
            Team team = teamMapper.selectById(registration.getTeamId());
            if (team != null && team.getMemberCount() == 1) {
                teamMapper.deleteById(registration.getTeamId());
            }
        }
        
        registrationMapper.update(registration);
        return registration;
    }

    @Override
    public List<Registration> getTeacherReviewList(Integer teacherId, String status) {
        return registrationMapper.getTeacherReviewList(teacherId, status);
    }

    @Override
    @Transactional
    public void approveRegistration(Integer registrationId, Integer teacherId) {
        // 1. 获取报名信息
        Registration registration = registrationMapper.getRegistrationDetail(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        
        // 2. 更新报名状态
        registrationMapper.updateStatus(registrationId, "approved", teacherId);
        
        // 3. 将学生添加到队伍成员中
        teamMemberMapper.insert(registration.getTeamId(), registration.getStudentId());
    }

    @Override
    public void rejectRegistration(Integer registrationId, Integer teacherId) {
        // 1. 获取报名信息
        Registration registration = registrationMapper.getRegistrationDetail(registrationId);
        if (registration == null) {
            throw new RuntimeException("报名记录不存在");
        }
        
        // 2. 更新报名状态为拒绝
        registrationMapper.updateStatus(registrationId, "rejected", teacherId);
    }
} 