package com.bjw.testtable.image.controller;

import com.bjw.testtable.file.service.FileService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class ImageUploadController {

    private final FileService fileService;

    // ✅ application.yml에 설정한 값을 읽어와서 변수에 주입합니다.
    @Value("${custom.upload.path}")
    private String uploadPath;

    @PostMapping("/image-upload")
    public void smartEditorImageUpload(
            @RequestParam("Filedata") MultipartFile file,
            HttpServletResponse response) throws IOException {

        try {
            if (file.isEmpty()) {
                response.getWriter().print("NOTALLOW_FileIsEmpty");
                return;
            }

            // --- 1. 파일 저장 로직 ---
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalFilename = file.getOriginalFilename();
            String savedFilename = UUID.randomUUID().toString() + "." + getExtension(originalFilename);
            Path destination = uploadDir.resolve(savedFilename);
            file.transferTo(destination);

            // --- 2. HTML5 업로더가 요구하는 순수 텍스트(Query String) 형식으로 응답 생성 ---
            String fileUrl = "/uploads/" + savedFilename;
            String encodedFilename = URLEncoder.encode(originalFilename, StandardCharsets.UTF_8);

            String responseBody = "sFileURL=" + fileUrl +
                    "&sFileName=" + encodedFilename +
                    "&bNewLine=true";

            // --- 3. 생성된 텍스트를 응답으로 전송 ---
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.print(responseBody);
            out.flush();

        } catch (Exception e) {
            // 예외 발생 시 간단한 텍스트로 응답
            response.setContentType("text/plain;charset=utf-8");
            PrintWriter out = response.getWriter();
            out.print("NOTALLOW_" + e.getMessage());
            out.flush();
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}