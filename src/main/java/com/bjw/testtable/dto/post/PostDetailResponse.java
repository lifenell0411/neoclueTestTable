package com.bjw.testtable.dto.post;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDetailResponse {
    private Long id;
    private String title;
    private String body;
    private String authorUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
}