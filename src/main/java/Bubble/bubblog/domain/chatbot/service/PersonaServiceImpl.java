package Bubble.bubblog.domain.chatbot.service;

import Bubble.bubblog.domain.chatbot.dto.req.PersonaRequestDTO;
import Bubble.bubblog.domain.chatbot.dto.res.PersonaResponseDTO;
import Bubble.bubblog.domain.chatbot.entity.Persona;
import Bubble.bubblog.domain.chatbot.repository.PersonaRepository;
import Bubble.bubblog.domain.user.entity.User;
import Bubble.bubblog.domain.user.repository.UserRepository;
import Bubble.bubblog.global.exception.CustomException;
import Bubble.bubblog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;
    private final UserRepository userRepository;

    // 말투 생성
    @Transactional
    public PersonaResponseDTO createPersona(PersonaRequestDTO request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Persona persona = Persona.of(request.getName(), request.getDescription(), user);
        personaRepository.save(persona);

        return PersonaResponseDTO.from(persona);
    }

    // 특정 말투 조회
    @Transactional(readOnly = true)
    @Override
    public PersonaResponseDTO getPersonaById(Long personaId) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERSONA_NOT_FOUND));

        return PersonaResponseDTO.from(persona);
    }

    // 말투 전체 조회
    @Transactional(readOnly = true)
    @Override
    public List<PersonaResponseDTO> getAllPersonas() {
        List<Persona> personas = personaRepository.findAll();
        return personas.stream().map(PersonaResponseDTO::from).toList();
    }

    // 특정 사용자의 말투 목록 조회
    @Transactional(readOnly = true)
    @Override
    public List<PersonaResponseDTO> getPersonasByUserId(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Persona> personas = personaRepository.findAllByUserId(userId);

        return personas.stream()
                .map(PersonaResponseDTO::from)
                .toList();
    }


    // 말투 수정
    @Transactional
    @Override
    public PersonaResponseDTO updatePersona(Long personaId, PersonaRequestDTO request, UUID userId) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERSONA_NOT_FOUND));

        persona.update(request.getName(), request.getDescription());
        return PersonaResponseDTO.from(persona);
    }

    // 말투 삭제
    @Transactional
    @Override
    public void deletePersona(Long personaId, UUID userId) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new CustomException(ErrorCode.PERSONA_NOT_FOUND));

        if (!persona.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_PERSONA_ACCESS);
        }

        personaRepository.delete(persona);
    }
}
