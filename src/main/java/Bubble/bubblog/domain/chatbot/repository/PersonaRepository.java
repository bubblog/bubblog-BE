package Bubble.bubblog.domain.chatbot.repository;

import Bubble.bubblog.domain.chatbot.entity.Persona;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

    @EntityGraph(attributePaths = {"user"})
    Optional<Persona> findByIdAndUserId(Long personaId, UUID userId);

    List<Persona> findAllByUserId(UUID userId);
}
