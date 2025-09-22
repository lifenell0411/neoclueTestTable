package com.bjw.testtable.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // application.yml (또는 properties)에 설정한 파일 저장 경로를 읽어옵니다.
    @Value("${custom.upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ 이 부분이 핵심입니다.
        // 브라우저에서 /uploads/ 로 시작하는 URL로 요청이 오면,
        // 서버의 uploadPath (예: C:/dev/uploads/) 폴더에서 파일을 찾아서 제공하라는 설정입니다.
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}