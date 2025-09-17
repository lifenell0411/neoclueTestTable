package com.bjw.testtable.file.repository;

import com.bjw.testtable.domain.file.QFileEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public List<Long> findPostIdsHavingFiles(Collection<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) return Collections.emptyList();

        QFileEntity f = QFileEntity.fileEntity; // Q-타입 이름은 네 프로젝트에 맞게

        return query
                .select(f.postId)   // ← 필드명이 다르면 맞춰 변경
                .from(f)
                .where(f.postId.in(postIds))
                .distinct()
                .fetch();
    }
}
