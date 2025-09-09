package com.bjw.testtable.domain.post.service;

import com.bjw.testtable.domain.post.dto.PostCreateRequest;
import com.bjw.testtable.domain.post.dto.PostDetailResponse;
import com.bjw.testtable.domain.post.dto.PostListResponse;
import com.bjw.testtable.domain.post.dto.PostUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

public interface PostService {
    Page<PostListResponse> list(String query, Pageable pageable);
    PostDetailResponse get(Long id);
    void update(Long id,
                String currentUserId,
                Collection<? extends GrantedAuthority> authorities,
                PostUpdateRequest req,
                List<MultipartFile> newFiles,
                List<Long> deleteFileIds);
    boolean canEdit(Long id, String currentUserId, Collection<? extends GrantedAuthority> authorities);
    void delete(Long id, String currentUserId,Collection<? extends GrantedAuthority> authorities);
    // service/PostService.java
    Long create(String currentUserId, PostCreateRequest req, List<MultipartFile> files);


}
