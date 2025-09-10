package com.bjw.testtable.config;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class AppErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest req) {
        Object codeObj = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int code = (codeObj instanceof Integer) ? (Integer) codeObj : 0;

        String message = switch (code) {
            case 404, 405 -> "존재하지 않는 페이지입니다.";
            case 403 -> "권한이 없습니다.";
            default -> "요청을 처리할 수 없습니다.";
        };

        String encoded = URLEncoder.encode(message, StandardCharsets.UTF_8);
        // 전부 리스트로
        return "redirect:/posts/list?error=" + encoded;
    }
}