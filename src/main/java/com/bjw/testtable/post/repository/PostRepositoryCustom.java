package com.bjw.testtable.post.repository;

import com.bjw.testtable.post.dto.PostListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface PostRepositoryCustom {
    Page<PostListResponse> search(String field, String query, Pageable pageable);
}