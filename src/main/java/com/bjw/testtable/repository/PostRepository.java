package com.bjw.testtable.repository;

import com.bjw.testtable.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 제목/본문 간단 검색
    @Query("""
  select p from Post p
  where (:q is null or :q = ''
         or p.title like concat('%', :q, '%')
         or cast(p.body as string) like concat('%', :q, '%'))
  order by p.id desc
""")
    Page<Post> search(@Param("q") String q, Pageable pageable);

}
