package microservices.core.product;

import org.junit.Test;
import org.mapstruct.factory.Mappers;
import se.kajzer.api.core.product.Product;
import se.kajzer.microservices.core.product.persistence.ProductEntity;
import se.kajzer.microservices.core.product.services.ProductMapper;

import static org.junit.Assert.*;

public class MapperTests {

    private ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    public void mapperTests() {
        assertNotNull(mapper);

        Product api = new Product(1, "n", 1, "sa");

        ProductEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getProductId(), entity.getProductId());
        assertEquals(api.getName(), entity.getName());
        assertEquals(api.getWeight(), entity.getWeight());

        Product api2 = mapper.entityToApi(entity);

        assertEquals(entity.getProductId(), api2.getProductId());
        assertEquals(entity.getName(), api2.getName());
        assertEquals(entity.getWeight(), api2.getWeight());
        assertNull(api2.getServiceAddress());
    }

}
