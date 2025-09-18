package com.bjw.testtable.file.repository;

import com.bjw.testtable.domain.file.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface FileRepository extends JpaRepository<FileEntity, Long>, FileRepositoryCustom {
    // 기본 JPA 메소드들 그대로 사용 + 커스텀 메소드 사용 가능


    List<FileEntity> findByPostId(Long postId);


    List<FileEntity> findByIdInAndPostIdAndDeletedFalse(List<Long> ids, Long postId);
    List<FileEntity> findByPostIdAndDeletedFalse(Long id);

}