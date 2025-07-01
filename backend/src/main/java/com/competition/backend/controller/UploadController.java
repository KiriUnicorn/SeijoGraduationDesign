package com.competition.backend.controller;

import com.competition.backend.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/upload")
public class UploadController {
    
    private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

    @Value("${upload.score.path}")
    private String uploadPath;

    @PostMapping("/score")
    public Result<String> uploadScoreCertificate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName) {
        try {
            logger.info("开始上传文件: {}", fileName);
            
            // 获取项目根目录
            String projectRoot = System.getProperty("user.dir");
            logger.info("项目根目录: {}", projectRoot);
            
            // 构建完整的上传路径
            String baseDir = "D:\\Study\\Swan\\Essay\\CompetitionManageSystem";
            Path uploadDir = Paths.get(baseDir, "frontend", "static", "images", "score");
            logger.info("上传目录: {}", uploadDir);
            
            // 确保上传目录存在
            if (!Files.exists(uploadDir)) {
                logger.info("创建上传目录");
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(fileName);
            logger.info("文件保存路径: {}", filePath);
            file.transferTo(filePath.toFile());

            // 返回相对路径
            String relativePath = "/static/images/score/" + fileName;
            logger.info("返回相对路径: {}", relativePath);
            return Result.success(relativePath);
        } catch (IOException e) {
            logger.error("文件上传失败", e);
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
} 