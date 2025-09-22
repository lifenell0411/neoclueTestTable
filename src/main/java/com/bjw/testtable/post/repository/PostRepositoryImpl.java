package com.bjw.testtable.post.repository;


import com.bjw.testtable.post.dto.PostListResponse;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
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

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<PostListResponse> search(String field, String query, Pageable pageable) {
        BooleanBuilder where = new BooleanBuilder().and(post.deleted.isFalse());

        String kw = (query == null) ? "" : query.trim();
        if (!kw.isEmpty()) {
            String like = "%" + kw.toLowerCase() + "%";
            // CLOB 안전 비교를 위해 body를 문자열로 캐스팅
            StringTemplate bodyStr = Expressions.stringTemplate("CAST({0} as string)", post.body);

            switch (field) {
                case "title" -> where.and(post.title.lower().like(like));
                case "body"  -> where.and(bodyStr.lower().like(like));
                case "user"  -> where.and(post.userId.lower().like(like));
                default -> where.and(
                        post.title.lower().like(like)
                                .or(bodyStr.lower().like(like))
                                .or(post.userId.lower().like(like))
                );
            }
        }

        // ✅ DTO 프로젝션 (DB에서는 자르지 않음)
        List<PostListResponse> content = queryFactory
                .select(Projections.bean(PostListResponse.class, //Post엔티티 통째로 말고 지정한 필드만 뽑아서 response객쳉에 담아서 줘라 //프로젝션
                        post.id.as("id"),
                        post.title.as("title"),
                        post.userId.as("authorUserId"),
                        post.body.as("bodyPreview"),     // 전체 본문을 일단 담고, 서비스에서 잘라줌
                        post.createdAt.as("createdAt"),
                        post.updateAt.as("updateAt")
                ))
                .from(post)
                .where(where)
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(where)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }
}
