package com.bjw.testtable.post.service;

import com.bjw.testtable.post.dto.PostCreateRequest;
import com.bjw.testtable.post.dto.PostDetailResponse;
import com.bjw.testtable.post.dto.PostListResponse;
import com.bjw.testtable.post.dto.PostUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

public interface PostService {
    Page<PostListResponse> list(String field, String query, Pageable pageable);
    PostDetailResponse get(Long id);
    void update(Long id,
                String currentUserId,
                PostUpdateRequest req,
                List<MultipartFile> newFiles,
                List<Long> deleteFileIds);
    void delete(Long id, String currentUserId,Collection<? extends GrantedAuthority> authorities);
    // service/PostService.java
    Long create(String currentUserId, PostCreateRequest req, List<MultipartFile> files);


}
