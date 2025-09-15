package com.bjw.testtable.util;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
public class HtmlSanitizer {

    private static final Safelist SAFE = Safelist.relaxed()
            .addTags("pre","code","blockquote","span") // 필요에 따라 추가
            .addAttributes("a","href","target","rel")
            .addProtocols("a","href","http","https","mailto")
            // 스타일/이벤트는 막는 걸 권장
            .removeAttributes(":all","style","onload","onclick","class","id");

    private HtmlSanitizer(){}

    public static String clean(String rawHtml){
        if (rawHtml == null) return "";
        String clean = Jsoup.clean(rawHtml, SAFE);
        // 제로폭 문자 제거(스마트에디터에서 종종 섞임)
        return clean.replaceAll("[\\u200B\\u200C\\u200D\\uFEFF]", "");
    }

    public static String toText(String html){
        return html == null ? "" : Jsoup.parse(html).text();
    }

    public static String preview(String plainText, int max){
        if (plainText == null) return "";
        return plainText.length() <= max ? plainText : plainText.substring(0, max) + "…";
    }

}
