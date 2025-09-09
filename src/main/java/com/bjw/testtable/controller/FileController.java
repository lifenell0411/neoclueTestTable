package com.bjw.testtable.controller;

import com.bjw.testtable.domain.file.dto.FileDownloadDto;
import com.bjw.testtable.domain.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/download") // ★ /files 대신 /download 로
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService; // ★ 반드시 final

    @GetMapping("/{id}") //파일을 다운로드할것이다, fileService에게 getFile시켜서 다운로드 기능 작동. 다운로드할때는 fileRepository에서 findByPostId 해서 fileentity를 찾고 filePath로 실제파일 읽어와서 FileDownloadDto로 변환?
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        FileDownloadDto dto = fileService.getFile(id);

        String filename = dto.getOriginalFilename() != null ? dto.getOriginalFilename() : "download";
        MediaType mediaType;
        try {
            mediaType = MediaType.parseMediaType(dto.getContentType());
        } catch (Exception e) {
            mediaType = MediaType.APPLICATION_OCTET_STREAM;
        }

        ContentDisposition cd = ContentDisposition
                .attachment()
                .filename(filename, StandardCharsets.UTF_8) // 한글 파일명 안전
                .build();

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, cd.toString())
                .body(dto.getResource());
    }
}