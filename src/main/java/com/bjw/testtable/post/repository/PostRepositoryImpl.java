package com.bjw.testtable.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.bjw.testtable.domain.post.Post;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.bjw.testtable.domain.post.QPost.post;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Page<Post> search(String q, Pageable pageable) {
        BooleanBuilder where = new BooleanBuilder();

        // 1) 삭제글 제외
        where.and(post.deleted.isFalse());

        // 2) q가 있으면 제목/본문 like (대소문자 무시)
        if (q != null && !q.trim().isEmpty()) {
            String like = "%" + q.trim().toLowerCase() + "%";
            where.and(
                    post.title.lower().like(like)
                            .or(post.body.lower().like(like))
            );
        }

        // 3) 조회 + 페이징
        List<Post> content = query
                .selectFrom(post)
                .where(where)
                .orderBy(post.id.desc())              // 기존 정렬 유지
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 4) total
        Long total = query
                .select(post.count())
                .from(post)
                .where(where)
                .fetchOne();
        long totalCount = (total == null) ? 0L : total;

        return new PageImpl<>(content, pageable, totalCount);
    }
}