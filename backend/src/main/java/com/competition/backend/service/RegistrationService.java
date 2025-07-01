package com.competition.backend.service;

import com.competition.backend.entity.Registration;
import java.util.List;

public interface RegistrationService {
    Registration createRegistration(Registration registration);
    Registration getRegistrationById(Integer registrationId);
    Registration updateRegistration(Registration registration);
    
    // 获取教师需要审核的报名列表
    List<Registration> getTeacherReviewList(Integer teacherId, String status);
    
    // 审核通过报名
    void approveRegistration(Integer registrationId, Integer teacherId);
    
    // 拒绝报名
    void rejectRegistration(Integer registrationId, Integer teacherId);
} 