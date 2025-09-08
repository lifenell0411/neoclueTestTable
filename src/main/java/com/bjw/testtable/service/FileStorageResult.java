package com.bjw.testtable.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

// service/FileStorageResult.java (record 또는 클래스)
@Getter
@AllArgsConstructor
public class FileStorageResult {
    private final String path;          // 실제 저장된 절대경로
    private final String contentType;   // MIME 타입 (null일 수도)
}