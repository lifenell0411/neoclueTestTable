package com.bjw.testtable.image.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;

@RestController // @Controller가 아닌 @RestController를 사용하면 JSON 반환이 편리합니다.
public class EditorApiController {

    @GetMapping("/api/editor/paths")
    public Map<String, String> getEditorPaths() {
        // 서버의 Context Path를 동적으로 가져와서 URL을 완성합니다.
        // 이렇게 하면 나중에 서버 URL 구조가 바뀌어도 코드를 수정할 필요가 없습니다.
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String contextPath = request.getContextPath();

        Map<String, String> paths = new HashMap<>();
        paths.put("jindo_js", contextPath + "/se2/sample/photo_uploader/jindo.min.js");
        paths.put("uploader_js", contextPath + "/se2/sample/photo_uploader/jindo.fileuploader.js");
        paths.put("attach_js", contextPath + "/se2/sample/photo_uploader/attach_photo.js");
        // 필요한 다른 경로들도 여기에 추가할 수 있습니다.

        return paths;
    }
}