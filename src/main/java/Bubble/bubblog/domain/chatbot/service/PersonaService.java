package Bubble.bubblog.domain.chatbot.service;

import Bubble.bubblog.domain.chatbot.dto.req.PersonaRequestDTO;
import Bubble.bubblog.domain.chatbot.dto.res.PersonaResponseDTO;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface PersonaService {
    PersonaResponseDTO createPersona(PersonaRequestDTO dto, UUID userId);
    List<PersonaResponseDTO> getPersonasByUserId(UUID userId);
    PersonaResponseDTO updatePersona(Long personaId, @Valid PersonaRequestDTO request, UUID userId);
    void deletePersona(Long personaId, UUID userId);

    PersonaResponseDTO getPersonaById(Long personaId, UUID userId);
}
