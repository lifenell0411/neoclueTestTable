package com.bjw.testtable.domain.post;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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
    private List<FileResponse> files;
}