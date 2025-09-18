package com.bjw.testtable.file.repository;

import com.bjw.testtable.domain.file.QFileEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FileRepositoryImpl implements FileRepositoryCustom {

    private final JPAQueryFactory query;
    private final QFileEntity file = QFileEntity.fileEntity;


    @Override
    public List<Long> findPostIdsHavingFiles(Collection<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) return List.of();
        return query
                .select(file.postId)
                .distinct()
                .from(file)
                .where(
                        file.deleted.isFalse(),
                        file.postId.in(postIds)
                )
                .fetch();
    }


}
