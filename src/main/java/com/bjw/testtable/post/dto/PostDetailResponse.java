package com.bjw.testtable.post.dto;

import com.bjw.testtable.domain.post.Post;
import com.bjw.testtable.file.dto.FileResponse;
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


    public static PostDetailResponse from(Post post, List<FileResponse> files) {
        return PostDetailResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .authorUserId(post.getUserId())
                .createdAt(post.getCreatedAt())
                .updateAt(post.getUpdateAt())
                .files(files)
                .build();
    }


}