package com.bjw.testtable.domain.post.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FileResponse {
    private Long id;
    private String originalFilename;
    private Long size;
    // 다운로드 링크가 있다면 미리 만들어 전달
    private String downloadUrl; // 예: /files/{id}
    // 필요하면 contentType, uploaderId 등 추가
}
