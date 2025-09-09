package com.bjw.testtable.domain.post.repository;

import com.bjw.testtable.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 제목/본문 간단 검색 // Post(엔티티 이름)는 p고 만약에 Param q에 아무것도 없으면 where조건 다 무시하고 그냥 order by. where 조건 있다면 조건에 맞는 게시글만 조회
    @Query("""
  select p from Post p 
  where coalesce(:q, '') = ''\s or lower(p.title) like concat('%', lower(:q), '%')
  or lower(p.body)  like concat('%', lower(:q), '%')
  order by p.id desc
""")
    Page<Post> search(@Param("q") String q, Pageable pageable);

}
