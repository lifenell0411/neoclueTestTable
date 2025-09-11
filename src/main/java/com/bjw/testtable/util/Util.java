package com.bjw.testtable.util;

import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;
@Component("html")
public class Util {



        public String preview(String html, int max) {
            if (html == null) html = "";
            String text = HtmlUtils.htmlUnescape(html.replaceAll("<[^>]*>", " "));
            text = text.replaceAll("\\s+", " ").trim();
            return text.length() > max ? text.substring(0, max) + "…" : text;
        }
    }

