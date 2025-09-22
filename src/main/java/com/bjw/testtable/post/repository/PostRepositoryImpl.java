package com.bjw.testtable.post.repository;


import com.bjw.testtable.post.dto.PostListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
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
//        BooleanBuilder where = new BooleanBuilder().and(post.deleted.isFalse());
//
//

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
                .where(post.deleted.isFalse(),              // 기본 조건
                        createSearchField(field, query))
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(post.count())
                .from(post)
                .where(post.deleted.isFalse(),              // 기본 조건
                        createSearchField(field, query))
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0L : total);
    }



    private BooleanExpression createSearchField(String field, String query) {
        String kw = (query == null) ? "" : query.trim();

        if (kw.isEmpty()) {
            return null; // 검색어가 없으면 조건을 추가하지 않음
        }
        String like = "%" + kw.toLowerCase() + "%";
        StringExpression bodyPlainLower = Expressions.stringTemplate(
                "lower(cast(" +
                        "  function('regexp_replace'," +                 // \s+ → ' '
                        "    function('regexp_replace'," +               // <태그> → ' '
                        "      function('replace', cast({0} as string), '&nbsp;', ' ')," +
                        "      '<[^>]*>', ' ')," +
                        "    '\\\\s+', ' ')" +
                        " as string))",
                post.body
        );

        return switch (field) {
            case "title" -> post.title.lower().like(like);
            case "body"  -> bodyPlainLower.like(like);
            case "user"  -> post.userId.lower().like(like);
            default      -> post.title.lower().like(like)
                    .or(bodyPlainLower.like(like))
                    .or(post.userId.lower().like(like));
        };
    }}
