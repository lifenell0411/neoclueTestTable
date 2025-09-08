package com.bjw.testtable.service;

import com.bjw.testtable.dto.post.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<PostListResponse> list(String query, Pageable pageable);
    PostDetailResponse get(Long id);
    Long create(String currentUserId, PostCreateRequest req);
    void update(Long id, String currentUserId, PostUpdateRequest req);
    void delete(Long id, String currentUserId);
}
