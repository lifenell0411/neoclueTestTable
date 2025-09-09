package com.bjw.testtable.domain.file.storage;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor //save에서 반환된 데이터값을 가지고있는중
public class FileStorageResult {
    private final String path;          // 실제 저장된 절대경로
    private final String contentType;   // MIME 타입 (null일 수도)
}