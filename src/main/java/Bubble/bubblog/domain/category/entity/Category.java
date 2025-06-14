package Bubble.bubblog.domain.category.entity;

import Bubble.bubblog.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_category_user"))
    @OnDelete(action = OnDeleteAction.CASCADE)
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