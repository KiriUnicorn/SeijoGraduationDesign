package com.competition.backend.config;

import com.competition.backend.excel.ExcelTemplateGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelTemplateInitializer implements CommandLineRunner {
    
    private final ExcelTemplateGenerator templateGenerator;
    
    @Override
    public void run(String... args) {
        try {
            // 生成Excel模板
            templateGenerator.generateStudentTemplate();
            templateGenerator.generateTeacherTemplate();
            log.info("Excel模板生成成功");
        } catch (IOException e) {
            log.error("生成Excel模板失败: {}", e.getMessage());
            // 不抛出异常，让应用继续启动
        } catch (Exception e) {
            log.error("生成Excel模板时发生未知错误: {}", e.getMessage());
            // 不抛出异常，让应用继续启动
        }
    }
} 