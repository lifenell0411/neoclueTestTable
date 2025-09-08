package com.bjw.testtable.repository;

import com.bjw.testtable.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileEntity, Long> {


    List<FileEntity> findByPostId(Long postId);


}