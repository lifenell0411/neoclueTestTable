package com.bjw.testtable.post.dto;

import com.bjw.testtable.domain.post.PostDetailResponse;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostListItemDto {
    private Long id;
    private String title;
    private String bodyPreview;   // 서버에서 계산 (HTML 제거 + 앞부분만)
    private String authorUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;



    // 🔸 추가: 상세 DTO → 리스트 변환기 (지금 반드시 필요)
    public static PostListItemDto fromDetail(PostDetailResponse r){
        PostListItemDto d = new PostListItemDto();
        d.setId(r.getId());
        d.setTitle(r.getTitle());
        d.setAuthorUserId(r.getAuthorUserId()); // 네 필드명 그대로 사용
        d.setBodyPreview(makePreview(r.getBody()));
        d.setCreatedAt(r.getCreatedAt());
        d.setUpdateAt(r.getUpdateAt());
        return d;
    }

    private static String makePreview(String html){
        if(html == null) return "";
        String text = html.replaceAll("<[^>]*>", ""); // 아주 단순한 태그 제거
        text = text.replaceAll("\\s+", " ").trim();
        return text.length() > 60 ? text.substring(0, 60) + "…" : text;
    }
}