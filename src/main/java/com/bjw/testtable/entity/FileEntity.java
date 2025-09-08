package com.bjw.testtable.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "`file`")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntity {

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
}