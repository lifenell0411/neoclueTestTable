package com.bjw.testtable.service;

import com.bjw.testtable.dto.post.PostCreateRequest;
import com.bjw.testtable.dto.post.PostDetailResponse;
import com.bjw.testtable.dto.post.PostListResponse;
import com.bjw.testtable.dto.post.PostUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {
    Page<PostListResponse> list(String query, Pageable pageable);
    PostDetailResponse get(Long id);
    void update(Long id, String currentUserId, PostUpdateRequest req);
    void delete(Long id, String currentUserId);
    // service/PostService.java
    Long create(String currentUserId, PostCreateRequest req, List<MultipartFile> files);

}
