package com.bjw.testtable.domain.post;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity //이건 엔티티다
@Table(name = "posts") //디비 테이블명
@Getter
@Setter
@NoArgsConstructor //생성자
@AllArgsConstructor
@Builder //빌더구조
public class Post {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) //오토인크리먼트
    private Long id;

    // posts.user_id → users.user_id (문자 FK)
    @Column(name="user_id", nullable = false, length = 40)
    private String userId; // 작성자

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String body;


    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="update_at", nullable = false)
    private LocalDateTime updateAt;


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


    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) createdAt = now;
        if (updateAt == null) updateAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updateAt = LocalDateTime.now();
    }
}