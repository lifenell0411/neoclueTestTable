package com.bjw.testtable.file.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public interface FileStorageService {
    FileStorageResult save(MultipartFile file);
    default void delete(String absolutePath) {
        try {
            if (absolutePath == null) return;
            Files.deleteIfExists(Paths.get(absolutePath));
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + absolutePath, e);
        }
    }
}
