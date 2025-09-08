package com.bjw.testtable.dto.post;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListResponse {
    private Long id;
    private String title;
    private String authorUserId;
    private String bodyPreview;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}