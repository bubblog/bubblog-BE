package Bubble.bubblog.domain.category.service;

import Bubble.bubblog.global.exception.CustomException;
import Bubble.bubblog.global.exception.ErrorCode;
import Bubble.bubblog.domain.category.dto.res.CategoryDTO;
import Bubble.bubblog.domain.category.dto.res.CategoryTreeDTO;
import Bubble.bubblog.domain.category.entity.Category;
import Bubble.bubblog.domain.category.entity.CategoryClosure;
import Bubble.bubblog.domain.category.repository.CategoryClosureRepository;
import Bubble.bubblog.domain.category.repository.CategoryRepository;
import Bubble.bubblog.domain.user.entity.User;
import Bubble.bubblog.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryClosureRepository closureRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CategoryDTO createCategory(String name, Long parentId, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ENTITY_NOT_FOUND));

        Category category = Category.of(name, user);
        categoryRepository.save(category);

        // 자기 자신과의 관계 depth=0
        closureRepository.save(CategoryClosure.of(category.getId(), category.getId(), 0));

        if (parentId != null && parentId > 0) {
            // parentId를 자손으로 가진 모든 CategoryClosure를 가져옴
            closureRepository.findAllByDescendantId(parentId)
                    .forEach(pc -> // parentId를 자손으로 가진 모든 엔티티에 대해
                            closureRepository.save( // 해당 엔티티의 부모를 생성하는 카테고리와 묶어 새로운 CategoryClosure생성
                                    CategoryClosure.of(pc.getAncestorId(), category.getId(), pc.getDepth() + 1)
                            )
                    );
        }

        return new CategoryDTO(category.getId(), category.getName());
    }

    @Override
    @Transactional
    public void updateCategory(Long categoryId, String newName, Long newParentId, UUID userId) {
        // 검증
        Category category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 이름 변경
        if (newName != null && !newName.isBlank()) {
            category.changeName(newName);
        }

        // 부모 변경
        if(newParentId != null && newParentId >= 0) {
            if (newParentId == 0) {
                // 수정하려는 카테고리를 상위카테고리로 가지는 모든 closure 가져오기
                List<Long> subtreeIds = closureRepository.findDescendantIds(categoryId);

                // 카테고리 리스트의 모든 자신이 하위인 관계중 카테고리 아래의 서브트리 제외의 관계를(자신, 자신, 0 제외) 제거
                // 즉, 수정하려는 카테고리가 루트인 서브트리는 유지한 채 상위와의 관계를 모두 제거!!!!!!!
                closureRepository.deleteExternalAncestors(subtreeIds);

                // 자기 자신 관계 저장
                subtreeIds.forEach(id ->
                        closureRepository.save(CategoryClosure.of(id, id, 0))
                );
            } else {
                // 자신을 부모로 지정 시 순환 발생
                if (newParentId.equals(categoryId)) {
                    throw new CustomException(ErrorCode.CATEGORY_SELF_PARENT);
                }

                List<Long> subtreeIds = closureRepository.findDescendantIds(categoryId);
                // 상위 카테고리를 상위로 지정 시 순환 발생
                if (subtreeIds.contains(newParentId)) {
                    throw new CustomException(ErrorCode.CATEGORY_CYCLE);
                }

                // newParentId가 유효한 카테고리인지, 그리고 같은 유저 소유인지 검증
                if (!categoryRepository.existsByIdAndUserId(newParentId, userId)) {
                    throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
                }

                // 카테고리 리스트의 모든 자신이 하위인 관계중 카테고리 아래의 서브트리 제외의 관계를(자신, 자신, 0 제외) 제거
                // 즉, 수정하려는 카테고리가 루트인 서브트리는 유지한 채 상위와의 관계를 모두 제거!!!!!!!
                closureRepository.deleteExternalAncestors(subtreeIds);

                // 원래 깊이 정보(origDepth) 조회
                // 서브 트리를 돌려 수정하려는 카테고리와의 높이를 계산
                Map<Long, Integer> origDepth = closureRepository
                        .findDepthsByAncestorAndDescendants(categoryId, subtreeIds)
                        .stream()
                        .collect(Collectors.toMap(
                                CategoryClosure::getDescendantId,
                                CategoryClosure::getDepth
                        ));

                // 새 부모 조상 목록 조회
                List<CategoryClosure> newParentAncestors = closureRepository.findAllByDescendantId(newParentId);

                // 새 조상 관계 추가 (서브트리 전체)
                for (Long descId : subtreeIds) {
                    // 자기 자신 관계 확인용 저장
                    closureRepository.save(CategoryClosure.of(descId, descId, 0));

                    // 새 부모 및 그 조상들과의 관계
                    int bToDescDepth = origDepth.getOrDefault(descId, 0);  // B→descId 깊이
                    for (CategoryClosure parentClosure : newParentAncestors) {
                        int newDepth = parentClosure.getDepth() + 1 + bToDescDepth;
                        closureRepository.save(CategoryClosure.of(
                                parentClosure.getAncestorId(),
                                descId,
                                newDepth
                        ));
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId, UUID userId) {
        // 검증
        categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 서브트리 ID 조회
        List<Long> subtreeIds = closureRepository.findDescendantIds(categoryId);

        // 클로저 관계 전부 삭제 (하위 포함)
        subtreeIds.forEach(closureRepository::deleteByDescendantId);

        // 엔티티 일괄 삭제
        categoryRepository.deleteAllById(subtreeIds);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDTO> getAllCategoriesAsDto(UUID userId) {
        // 유저의 카테고리를 리스트로 반환
        return categoryRepository.findAllByUserId(userId).stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName()))
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryTreeDTO> getAllCategoriesAsTree(UUID userId) {
        // 유저의 카테고리를 리스트로 받음
        List<Category> categories = categoryRepository.findAllByUserId(userId);
        // 트리 구조맵 초기화
        Map<Long, CategoryTreeDTO> dtoMap = new HashMap<>();
        // 유저의 카테고리를 트리로 저장
        categories.forEach(category -> dtoMap.put(category.getId(), new CategoryTreeDTO(category.getId(), category.getName())));
        // 부모-자식 관계로 등록
        categories.forEach(category ->
                closureRepository.findDirectParentId(category.getId())
                        .ifPresent(parentId -> dtoMap.get(parentId).addChild(dtoMap.get(category.getId())))
        );

        // 루트 노드들에 root 플래그 세팅
        dtoMap.values().forEach(dto -> {
            if (closureRepository.findDirectParentId(dto.getId()).isEmpty()) {
                dto.setAsRoot();
            }
        });

        // 루트인 걸로 묶어 리스트로 반환
        return dtoMap.values().stream().filter(CategoryTreeDTO::isRoot).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryTreeDTO getCategoryWithDescendants(Long categoryId, UUID userId) {
        // 검증
        categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 해당 카테고리의 모든 하위 카테고리 아이디 조회
        List<Long> descendantIds = closureRepository.findDescendantIds(categoryId);
        // 카테고리와 하위 카테고리와의 모든 관계 즉, 서브 트리 리스트로 조회
        List<Category> categories = categoryRepository.findByIdInAndUserId(descendantIds, userId);
        Map<Long, CategoryTreeDTO> dtoMap = new HashMap<>();
        // 트리 구조로 생성
        categories.forEach(category -> dtoMap.put(category.getId(), new CategoryTreeDTO(category.getId(), category.getName())));
        CategoryTreeDTO root = dtoMap.get(categoryId);
        // 최상단 루트 등록
        root.setAsRoot();
        // 부모-자식 등록
        categories.forEach(category ->
                closureRepository.findDirectParentId(category.getId())
                        .ifPresent(parentId -> dtoMap.get(parentId).addChild(dtoMap.get(category.getId())))
        );
        return root;
    }
}