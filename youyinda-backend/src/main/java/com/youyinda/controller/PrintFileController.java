package com.youyinda.controller;

import com.youyinda.common.BusinessException;
import com.youyinda.common.R;
import com.youyinda.entity.FileInfo;
import com.youyinda.service.FileInfoService;
import com.youyinda.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 打印文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/v1/print/file")
public class PrintFileController {

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    @Autowired
    private FileInfoService fileInfoService;

    private Path uploadDir;

    @PostConstruct
    public void init() {
        uploadDir = Paths.get(uploadPath).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            log.error("无法创建上传目录: {}", uploadDir, e);
        }
    }

    /**
     * 上传打印文件
     * @param file 文件
     * @return 文件信息
     */
    @PostMapping("/upload")
    public R<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = JwtUtil.getUserIdFromToken();
            if (userId == null) {
                throw new BusinessException(401, "未登录");
            }

            if (file.isEmpty()) {
                throw new BusinessException(400, "文件为空");
            }

            // 生成日期子目录
            String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            Path targetDir = uploadDir.resolve(dateDir);
            Files.createDirectories(targetDir);

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFileName = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path targetPath = targetDir.resolve(newFileName);
            file.transferTo(targetPath.toFile());

            // 记录文件信息
            FileInfo fileInfo = new FileInfo();
            fileInfo.setUserId(userId);
            fileInfo.setFileName(originalFilename);
            fileInfo.setFileUrl("/uploads/" + dateDir + "/" + newFileName);
            fileInfo.setFilePath(targetPath.toString());
            fileInfo.setFileSize(file.getSize());
            fileInfo.setFileType(extension.replace(".", ""));
            fileInfoService.save(fileInfo);

            Map<String, Object> result = new HashMap<>();
            result.put("fileId", fileInfo.getId());
            result.put("fileName", originalFilename);
            result.put("fileUrl", fileInfo.getFileUrl());
            result.put("fileSize", file.getSize());

            log.info("文件上传成功: userId={}, file={}, size={}", userId, originalFilename, file.getSize());
            return R.success(result);

        } catch (BusinessException e) {
            throw e;
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new BusinessException(500, "文件上传失败");
        }
    }

    /**
     * 批量上传文件
     */
    @PostMapping("/batch-upload")
    public R<List<Map<String, Object>>> batchUpload(@RequestParam("files") MultipartFile[] files) {
        List<Map<String, Object>> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(uploadFile(file).getData());
        }
        return R.success(results);
    }
}
