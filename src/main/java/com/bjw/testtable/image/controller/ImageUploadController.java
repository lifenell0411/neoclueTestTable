package com.bjw.testtable.image.controller;

import com.bjw.testtable.domain.file.FileEntity;
import com.bjw.testtable.file.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;


@Controller
@RequiredArgsConstructor
public class ImageUploadController {

    private final FileService fileService; // 새로 만든 통합 서비스를 주입받습니다.



    @PostMapping("/image-upload")
    public void smartEditorImageUpload(@RequestParam("Filedata") MultipartFile file, HttpServletRequest request,HttpServletResponse response) throws IOException {

        // 실제로는 SecurityContext 등에서 로그인한 사용자 정보를 가져와야 합니다.
        // 게시글 ID는 form 데이터나 다른 파라미터로 받아야 합니다.
        String currentUserId = "testuser";
        Long currentPostId = 1L; // 예시 게시글 ID

        // 1. 통합 서비스를 호출하여 파일 저장 + DB 기록을 한 번에 처리!
        FileEntity savedFile = fileService.uploadFile(file, currentPostId, currentUserId);

        // 2. 스마트에디터 콜백 로직 (기존과 동일)
        //    이제 savedFile 객체에서 파일 경로 등 모든 정보를 가져올 수 있습니다.
        response.setContentType("text/html;charset=utf-8");
        PrintWriter out = response.getWriter();

        // 주의: savedFile.getFilepath()는 절대 경로이므로, 웹에서 접근 가능한 상대 URL로 변환해야 합니다.
        // WebMvcConfig에 설정한 /uploads/ 와 파일명을 조합합니다.
        String filename = savedFile.getFilepath().substring(savedFile.getFilepath().lastIndexOf(File.separator) + 1);
        String imageUrl = "/uploads/" + filename; // 예: /uploads/uuid.jpg

        String callback = request.getParameter("callback");
        String callback_func = request.getParameter("callback_func");

        out.println("<script type='text/javascript'>");
        out.println("window.parent.frames['se2_iframe_" + callback + "']."+callback_func+"(");
        out.println("'"+imageUrl+"', '"+savedFile.getOriginalFilename()+"'");
        out.println(");");
        out.println("</script>");
        out.flush();
    }

}
