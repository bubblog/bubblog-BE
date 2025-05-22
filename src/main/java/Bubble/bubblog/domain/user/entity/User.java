package Bubble.bubblog.domain.user.entity;

import Bubble.bubblog.domain.category.entity.Category;
import Bubble.bubblog.domain.post.entity.BlogPost;
import Bubble.bubblog.domain.user.dto.req.SignupRequestDTO;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Column(nullable = false, unique = true, length = 30)
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<BlogPost> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Category> categories = new ArrayList<>();

    // 프로필 이미지 설정하고 user 생성
    private static User of(String email, String password, String nickname, String profileImageUrl) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.nickname = nickname;
        user.profileImageUrl = profileImageUrl;

        return user;
    }

    // 프로필 이미지 설정 안하고 user 생성
    private static User of(String email, String password, String nickname) {
        return of(email, password, nickname, null);
    }

    public static User from(SignupRequestDTO dto, PasswordEncoder passwordEncoder) {
        return User.of(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                dto.getNickname(),
                dto.getProfileImageUrl()
        );
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }


}
