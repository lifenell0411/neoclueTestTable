package com.bjw.testtable.post.repository;

import com.bjw.testtable.domain.post.PostListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface PostRepositoryCustom {
    Page<PostListResponse> search(String field, String q, Pageable pageable);
}