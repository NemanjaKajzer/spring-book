package se.kajzer.microservices.core.review.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import se.kajzer.api.core.review.Review;
import se.kajzer.api.core.review.ReviewService;
import se.kajzer.microservices.core.review.persistence.ReviewEntity;
import se.kajzer.microservices.core.review.persistence.ReviewRepository;
import se.kajzer.util.exceptions.InvalidInputException;
import se.kajzer.util.http.ServiceUtil;

import java.util.List;

@RestController
public class ReviewServiceImpl implements ReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ReviewRepository repository;
    private final ReviewMapper mapper;

    @Autowired
    public ReviewServiceImpl(ServiceUtil serviceUtil, ReviewRepository repository, ReviewMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }


    @Override
    public List<Review> getReviews(int productId) {
        if(productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        List<ReviewEntity> reviewEntities = repository.findByProductId(productId);

        List<Review> reviews = mapper.entityListToApiList(reviewEntities);

        reviews.forEach(review -> review.setServiceAddress(serviceUtil.getServiceAddress()));

        return reviews;
    }

    @Override
    public Review createReview(Review body) {
        try {
            ReviewEntity reviewEntity = mapper.apiToEntity(body);
            repository.save(reviewEntity);

            LOG.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
            return mapper.entityToApi(reviewEntity);
        } catch (DataIntegrityViolationException dive){
            throw new InvalidInputException("Duplicate key: " + body.getProductId());
        }
    }

    @Override
    public void deleteReview(int reviewId) {
        if(reviewId < 1) throw new InvalidInputException("Invalid reviewId: " + reviewId);

        LOG.debug("deleteReview: tries to delete reviews with reviewId: {}", reviewId);
        repository.findByReviewId(reviewId).ifPresent(reviewEntity -> repository.delete(reviewEntity));
    }

    @Override
    public void deleteReviews(int productId) {
        if(productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

        LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
