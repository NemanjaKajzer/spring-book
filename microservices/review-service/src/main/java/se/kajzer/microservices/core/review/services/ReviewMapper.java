package se.kajzer.microservices.core.review.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.kajzer.api.core.review.Review;
import se.kajzer.microservices.core.review.persistence.ReviewEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Review entityToApi(ReviewEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    ReviewEntity apiToEntity(Review api);

    List<Review> entityListToApiList(List<ReviewEntity> reviewEntities);

    List<ReviewEntity> apiListToEntityList(List<Review> reviews);
}
