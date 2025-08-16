package Bubble.bubblog.domain.tag.service;

import Bubble.bubblog.domain.tag.dto.res.TagResponseDTO;
import Bubble.bubblog.domain.tag.entity.Tag;
import Bubble.bubblog.domain.tag.repository.TagRepository;
import Bubble.bubblog.global.exception.CustomException;
import Bubble.bubblog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    //private final BlogPostRepository blogPostRepository;
    //private final PostTagRepository postTagRepository;

    // 태그 전체 조회
    @Override
    @Transactional(readOnly = true)
    public List<TagResponseDTO> getAllTags() {
        return tagRepository.findAll().stream()
                .map(TagResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 태그 조회
    @Override
    @Transactional(readOnly = true)
    public TagResponseDTO getTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));
        return new TagResponseDTO(tag);
    }

// 게시글 생성 시 태그 자동 추출해서 필요없음 - 필요할까봐 미리 만든 API
//    // 태그 생성
//    @Override
//    @Transactional
//    public TagResponseDTO createTag(TagRequestDTO request) {
//        // 중복 이름 체크
//        tagRepository.findByName(request.getName()).ifPresent(tag -> {
//            throw new CustomException(ErrorCode.DUPLICATE_TAG);
//        });
//
//        Tag tag = tagRepository.save(new Tag(request.getName()));
//        return new TagResponseDTO(tag);
//    }

// 현재는 게시글 상세 조회에서 DTO에 태그를 포함하는데 혹시 필요할까봐 작성한 API
//    // 특정 게시글의 태그 목록 조회
//    @Override
//    @Transactional(readOnly = true)
//    public List<TagResponseDTO> getTagsForPost(Long postId) {
//        BlogPost post = blogPostRepository.findById(postId)
//                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
//
//        List<PostTag> postTags = postTagRepository.findAllByPost(post);
//
//        return postTags.stream()
//                .map(pt -> new TagResponseDTO(pt.getTag()))
//                .collect(Collectors.toList());
//    }
}

