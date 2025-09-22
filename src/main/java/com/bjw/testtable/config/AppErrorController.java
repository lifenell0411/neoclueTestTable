package com.bjw.testtable.config;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;

@Controller
public class AppErrorController implements ErrorController {

//    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
//    public String handleErrorHtml(HttpServletRequest req, RedirectAttributes ra) {
//        Object codeObj = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
//        int code = (codeObj instanceof Integer) ? (Integer) codeObj : 0;
//
//        String message = switch (code) {
//            case 404, 405 -> "존재하지 않는 페이지입니다.";
//            case 403 -> "권한이 없습니다.";
//            default -> "요청을 처리할 수 없습니다.";
//        };
//
//        ra.addFlashAttribute("error", message);
//        return "redirect:/posts/list";
//    }
}