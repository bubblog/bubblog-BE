package Bubble.bubblog.domain.tag.service;

import Bubble.bubblog.domain.tag.dto.res.TagResponseDTO;

import java.util.List;

public interface TagService {
    List<TagResponseDTO> getAllTags();
    TagResponseDTO getTag(Long id);

//    게시글 생성 시 태그 자동 추출해서 필요없음 - 필요할까봐 미리 만든 API
//    TagResponseDTO createTag(TagRequestDTO request);

// 현재는 게시글 상세 조회에서 DTO에 태그를 포함하는데 혹시 필요할까봐 작성한 API
//    // 특정 게시글에 연결된 태그 목록 조회
//    List<TagResponseDTO> getTagsForPost(Long postId);
}
