package com.bjw.testtable.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    FileStorageResult save(MultipartFile file);
}
