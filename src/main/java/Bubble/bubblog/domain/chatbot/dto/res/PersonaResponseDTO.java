package Bubble.bubblog.domain.chatbot.dto.res;

import Bubble.bubblog.domain.chatbot.entity.Persona;
import lombok.Getter;

@Getter
public class PersonaResponseDTO {
    private final Long id;
    private final String name;
    private final String description;

    private PersonaResponseDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static PersonaResponseDTO from(Persona persona) {
        return new PersonaResponseDTO(
                persona.getId(),
                persona.getName(),
                persona.getDescription()
        );
    }
}
