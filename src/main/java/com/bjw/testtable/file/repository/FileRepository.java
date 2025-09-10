package com.bjw.testtable.file.repository;

import com.bjw.testtable.domain.file.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {


    List<FileEntity> findByPostId(Long postId);

    // 삭제 체크박스 처리용(내 글의 파일만)
    List<FileEntity> findByIdInAndPostId(List<Long> ids, Long postId);
}