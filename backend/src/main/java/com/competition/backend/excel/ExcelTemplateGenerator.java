package com.competition.backend.excel;

import com.alibaba.excel.EasyExcel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ExcelTemplateGenerator {
    
    public void generateStudentTemplate() throws IOException {
        // 获取resources目录下的templates文件夹
        File templateDir = new File("src/main/resources/templates");
        if (!templateDir.exists()) {
            templateDir.mkdirs();
        }
        
        String fileName = templateDir.getAbsolutePath() + File.separator + "student_import_template.xlsx";
        List<UserImportData> dataList = new ArrayList<>();
        
        // 添加示例数据
        UserImportData example = new UserImportData();
        example.setName("张三");
        example.setEmail("zhangsan@example.com");
        example.setStudentId(2023001);
        example.setDepartmentId(1);
        dataList.add(example);
        
        // 写入Excel
        EasyExcel.write(fileName, UserImportData.class)
                .sheet("学生导入模板")
                .doWrite(dataList);
    }
    
    public void generateTeacherTemplate() throws IOException {
        // 获取resources目录下的templates文件夹
        File templateDir = new File("src/main/resources/templates");
        if (!templateDir.exists()) {
            templateDir.mkdirs();
        }
        
        String fileName = templateDir.getAbsolutePath() + File.separator + "teacher_import_template.xlsx";
        List<UserImportData> dataList = new ArrayList<>();
        
        // 添加示例数据
        UserImportData example = new UserImportData();
        example.setName("李四");
        example.setEmail("lisi@example.com");
        example.setTeacherId(2023001);
        example.setDepartmentId(1);
        dataList.add(example);
        
        // 写入Excel
        EasyExcel.write(fileName, UserImportData.class)
                .sheet("教师导入模板")
                .doWrite(dataList);
    }
} 