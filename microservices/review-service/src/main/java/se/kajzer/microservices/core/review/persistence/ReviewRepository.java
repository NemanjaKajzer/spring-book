package se.kajzer.microservices.core.review.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> {
    @Transactional(readOnly = true)
    List<ReviewEntity> findByProductId(int productId);
    Optional<ReviewEntity> findByReviewId(int reviewId);
}
