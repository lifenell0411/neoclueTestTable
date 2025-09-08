package com.bjw.testtable.controller;

import com.bjw.testtable.dto.post.file.FileDownloadDto;
import com.bjw.testtable.service.FileService;
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

    @GetMapping("/{id}")
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