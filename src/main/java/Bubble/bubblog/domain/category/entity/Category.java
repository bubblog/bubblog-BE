package Bubble.bubblog.domain.category.entity;

import Bubble.bubblog.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Category(String name, User user) {
        this.name = name;
        this.user = user;
    }

    public static Category of(String name, User user) {
        return new Category(name, user);
    }

    public void changeName(String newName) {
        this.name = newName;
    }
}