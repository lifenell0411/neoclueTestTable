package com.bjw.testtable.file.dto;

import com.bjw.testtable.domain.file.FileEntity;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FileResponse {
    private Long id;
    private String originalFilename;
    private Long size;
    // 다운로드 링크가 있다면 미리 만들어 전달
    private String downloadUrl; // 예: /files/{id}
    // 필요하면 contentType, uploaderId 등 추가


    public static FileResponse from(FileEntity fileEntity) {
        return FileResponse.builder()
                .id(fileEntity.getId())
                .originalFilename(fileEntity.getOriginalFilename())
                .size(fileEntity.getSize())
                .build();
    }


}
