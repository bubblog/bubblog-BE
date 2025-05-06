package Bubble.bubblog.domain.category.repository;

import Bubble.bubblog.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByUserId(UUID userId);

    boolean existsByIdAndUserId(Long id, UUID userId);

    List<Category> findByIdInAndUserId(List<Long> ids, UUID userId);

    Optional<Category> findByIdAndUserId(Long id, UUID userId);
}