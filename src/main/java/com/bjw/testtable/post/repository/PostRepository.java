package com.bjw.testtable.post.repository;

import com.bjw.testtable.domain.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {


}
