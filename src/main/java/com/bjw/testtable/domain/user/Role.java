package com.bjw.testtable.domain.user;


import jakarta.persistence.*;
import lombok.*;

@Entity //이것은 엔티티다
@Table(name = "role") //테이블매칭
@Getter
@Setter           // getter, setter 자동 생성
@NoArgsConstructor        // 기본 생성자 자동 생성
@AllArgsConstructor       // 모든 필드 생성자 자동 생성
@Builder                  // 빌더 패턴 자동 생성
public class Role {
    @Id //이 필드가 엔티티의 PK
    @GeneratedValue(strategy = GenerationType.IDENTITY) //auto_increment 사용
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

}