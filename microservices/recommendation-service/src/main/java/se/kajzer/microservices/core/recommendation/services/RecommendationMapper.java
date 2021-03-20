package se.kajzer.microservices.core.recommendation.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import se.kajzer.api.core.recommendation.Recommendation;
import se.kajzer.microservices.core.recommendation.persistence.RecommendationEntity;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mappings({
            @Mapping(target = "rate", source = "entity.rating"),
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Recommendation entityToApi(RecommendationEntity entity);


    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true),
            @Mapping(target = "rating", source = "api.rate")
    })
    RecommendationEntity apiToEntity(Recommendation api);
}
