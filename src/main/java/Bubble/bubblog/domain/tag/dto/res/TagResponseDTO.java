package Bubble.bubblog.domain.tag.dto.res;

import Bubble.bubblog.domain.tag.entity.Tag;
import lombok.Getter;

@Getter
public class TagResponseDTO {
    private Long id;
    private String name;

    public TagResponseDTO(Tag tag) {   // new TagResponseDTO(tag)로 DTO 생성
        this.id = tag.getId();
        this.name = tag.getName();
    }
}
