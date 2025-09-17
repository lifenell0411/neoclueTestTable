package com.bjw.testtable.util;

import org.springframework.stereotype.Component;
@Component("html")
public class Util {


    private Util() {}
    public static String preview(String s, int max) {
        if (s == null) return "";
        String plain = s.replaceAll("<[^>]*>", " ")  // 태그 제거(본문이 HTML일 수 있으면)
                .replace("&nbsp;", " ")
                .replaceAll("\\s+", " ")
                .trim();
        return (plain.length() > max) ? plain.substring(0, max) + "..." : plain;
    }
}
