package com.bjw.testtable.domain.file;

import com.bjw.testtable.file.storage.FileStorageResult;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Entity
@Table(name = "`file`")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntity { //DB테이블 file의 1:1매핑. JPA가 관리, FileRepository와 FileServiceImpl에서 사용

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 단순 FK
    @Column(name = "post_id", nullable = false)
    private Long postId;

    @Column(name = "user_id", nullable = false, length = 40)
    private String userId;

    @Column(nullable = false, length = 500)
    private String filepath;

    @Column(name = "content_type", nullable = false, length = 127)
    private String contentType;

    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    @Column(name = "size")
    private Long size;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    @Column(nullable = false)
    private boolean deleted = false;

    private LocalDateTime deletedAt;
    private String deletedBy;

    public void markDeleted(String userId){
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = userId;
    }

    public void restore(){
        this.deleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
    }
    public static FileEntity create(MultipartFile file,
                                    FileStorageResult storageResult,
                                    Long postId,
                                    String userId) {
        return FileEntity.builder()
                .postId(postId)
                .userId(userId)
                .filepath(storageResult.getPath())
                .contentType(storageResult.getContentType())
                .originalFilename(file.getOriginalFilename())
                .size(file.getSize())
                .build();
    }

}