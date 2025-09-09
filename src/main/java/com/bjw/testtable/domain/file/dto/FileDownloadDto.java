package com.bjw.testtable.domain.file.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;


@Getter
@AllArgsConstructor
public class FileDownloadDto { //파일다운로드 응답에 필요한 값만 받는 전달객체(필요한데이터만) fileController에서 다운로드를 하기 위해 fileService가 만들어줌
    private final Resource resource;
    private final String originalFilename;
    private final String contentType;
}