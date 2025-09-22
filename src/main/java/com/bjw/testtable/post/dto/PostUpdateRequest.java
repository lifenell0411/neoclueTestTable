package com.bjw.testtable.post.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequest {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;
    @NotBlank(message = "내용은 필수입니다.")
    private String body;
    // 체크박스 name="deleteFileIds" 로 넘어오는 값 바인딩
    private List<Long> deleteFileIds;
}