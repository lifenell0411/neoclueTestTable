package com.bjw.testtable.domain.user;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor        // 기본 생성자 자동 생성
@AllArgsConstructor       // 모든 필드 생성자 자동 생성
@Builder

public class AppUser {

    @Id //이 필드가 엔티티의 PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto_increment 사용
    private Long id;

    @Column(name = "user_id", nullable = false, length = 40, unique = true) //db직접관리하면 굳이 안써도 되는데 가독성높아짐. 혹은 ddl생성할때 적어주는게 좋다고함
    private String userId;

    @Column(nullable = false, length = 255)
    private String password; // 반드시 BCrypt로 저장

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role", nullable = false) // users.role → role.id
    private Role role;
}