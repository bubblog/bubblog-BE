package Bubble.bubblog.global.util.queryDSL;

import Bubble.bubblog.global.exception.CustomException;
import Bubble.bubblog.global.exception.ErrorCode;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public class QueryDslUtils {
    // 동적 정렬 처리
    public static <T> void applySorting(JPAQuery<T> query, Pageable pageable, Class<T> entityClass, String alias) {
        PathBuilder<T> path = new PathBuilder<>(entityClass, alias);

        for (Sort.Order order : pageable.getSort()) {
            try {
                query.orderBy(order.isAscending()
                        ? path.getComparable(order.getProperty(), Comparable.class).asc()
                        : path.getComparable(order.getProperty(), Comparable.class).desc());
            } catch (IllegalArgumentException e) {
                throw new CustomException(ErrorCode.INVALID_SORT_FIELD);
            }
        }
    }

    // 페이징 처리
    public static <T> Page<T> getPageResult(JPAQuery<T> contentQuery,
                                            JPAQuery<?> countQuery,
                                            Pageable pageable) {
        List<T> results = contentQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.<Long>ofNullable((Long)countQuery.fetchOne()).orElse(0L);

        return new PageImpl<>(results, pageable, total);
    }
}

