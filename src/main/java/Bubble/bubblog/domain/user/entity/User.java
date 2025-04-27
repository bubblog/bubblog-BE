package Bubble.bubblog.domain.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity     // 현재 클래스가 JPA 엔티티임을 선언 -> 이 클래스를 기반으로 DB 테이블을 생성 및 조작 가능. JPA가 이 클래스를 보고 users 테이블과 매핑
@Table(name = "users")   // 해당 엔티티가 매핑될 테이블 명 users
public class User {

    @Id     // primary key임을 나타내는 어노테이션
    // UUID 생성을 위한 설정
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    // Column 어노테이션 - 각 필드가 DB의 어떤 컬럼과 매핑되는지 정의
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
