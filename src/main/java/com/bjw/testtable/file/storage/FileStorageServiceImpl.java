package com.bjw.testtable.file.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    // application.yml에 지정할 업로드 루트
    @Value("${app.upload.dir:/tmp/uploads}")
    private String uploadDir;

    @Override
    public FileStorageResult save(MultipartFile file) {
        try {
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null) {
                int dot = original.lastIndexOf('.');
                if (dot != -1) ext = original.substring(dot);
            }

            String uuid = UUID.randomUUID().toString().replace("-", "");
            String ymd = LocalDate.now().toString(); // e.g. 2025-09-08

            Path dir = Paths.get(uploadDir, ymd);
            Files.createDirectories(dir);

            Path target = dir.resolve(uuid + ext);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            String contentType = file.getContentType();
            if (contentType == null) contentType = "application/octet-stream";

            return new FileStorageResult(target.toString(), contentType); //여기서 저장된 file의 path와 contentType을 Result로 반환.

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}
