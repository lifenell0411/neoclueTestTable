package com.bjw.testtable.post.repository;

import com.bjw.testtable.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface PostRepositoryCustom {
    Page<Post> search(String q, Pageable pageable);
}