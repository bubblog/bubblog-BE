package Bubble.bubblog.domain.chatbot.entity;

import Bubble.bubblog.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Persona")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;  // 말투 명

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;                 // 말투에 대한 설명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Persona(String name, String description, User user) {
        this.name = name;
        this.description = description;
        this.user = user;
    }

    public static Persona of(String name, String description, User user) {
        return new Persona(name, description, user);
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
