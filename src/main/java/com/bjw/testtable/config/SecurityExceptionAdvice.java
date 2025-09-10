
package com.bjw.testtable.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(annotations = Controller.class)
public class SecurityExceptionAdvice {

    @ExceptionHandler({ AccessDeniedException.class, AuthorizationDeniedException.class })
    public String handleAccessDenied(Exception ex,
                                     HttpServletRequest request,
                                     RedirectAttributes ra) {
        ra.addFlashAttribute("error", "권한이 없습니다.");
        // 상황에 따라 상세로 보낼지, 목록으로 보낼지 선택
        // 예: /posts/{id}/update 같은 URL이면 상세로 보내고, 아니면 목록
        String uri = request.getRequestURI(); // e.g. /posts/32/update
        if (uri != null && uri.matches("^/posts/\\d+/.+")) {
            String id = uri.replaceAll("^/posts/(\\d+)/.*", "$1");
            // 상세로 돌리고 싶으면 ↓
            // return "redirect:/posts/" + id;
        }

        return "redirect:/posts/list";
    }
}
