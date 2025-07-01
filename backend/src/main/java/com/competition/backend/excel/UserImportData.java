package com.competition.backend.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class UserImportData {
    
    @ExcelProperty("姓名")
    private String name;
    
    @ExcelProperty("邮箱")
    private String email;
    
    @ExcelProperty("学号")
    private Integer studentId;
    
    @ExcelProperty("工号")
    private Integer teacherId;
    
    @ExcelProperty("院系ID")
    private Integer departmentId;
} 