package se.kajzer.microservices.core.product;

import org.springframework.dao.DuplicateKeyException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import se.kajzer.microservices.core.product.persistence.ProductEntity;
import se.kajzer.microservices.core.product.persistence.ProductRepository;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class PersistenceTests {

    @Autowired
    private ProductRepository repository;

    private ProductEntity savedEntity;

    @Before
    public void setupDb() {
        repository.deleteAll();

        ProductEntity entity = new ProductEntity(1, "n", 1);
        savedEntity = repository.save(entity);

        assertEqualsProduct(entity, savedEntity);
    }

    @Test
    public void createTest() {
        ProductEntity entity = new ProductEntity(2, "n2", 2);

        repository.save(entity);

        ProductEntity foundEntity = repository.findById(entity.getId()).get();
        assertEqualsProduct(entity, foundEntity);

        assertEquals(2, repository.count());
    }

    @Test
    public void getByProductIdTest() {
        Optional<ProductEntity> foundEntity = repository.findByProductId(savedEntity.getProductId());

        assertTrue(foundEntity.isPresent());
        assertEqualsProduct(savedEntity, foundEntity.get());
    }

    @Test
    public void updateTest() {
        savedEntity.setName("n2");
        repository.save(savedEntity);

        ProductEntity foundEntity = repository.findById(savedEntity.getId()).get();

        assertEquals(foundEntity.getName(), savedEntity.getName());
        assertEquals(1, (long) foundEntity.getVersion());
    }

    @Test
    public void deleteTest() {
        repository.delete(savedEntity);
        assertFalse(repository.existsById(savedEntity.getId()));
    }

    @Test(expected = DuplicateKeyException.class)
    public void duplicateIdErrorTest() {
        ProductEntity duplicate = new ProductEntity(savedEntity.getProductId(), "n", 1);
        repository.save(duplicate);
    }

    @Test
    public void optimisticLockErrorTest() {
        // Store the saved entity in two separate entity objects
        ProductEntity entity1 = repository.findById(savedEntity.getId()).get();
        ProductEntity entity2 = repository.findById(savedEntity.getId()).get();

        // Update the entity using the first entity object
        entity1.setName("n1");
        repository.save(entity1);

        try {
            entity2.setName("n2");
            repository.save(entity2);

            fail("Expected an OptimisticLockingFailureException");
        } catch (OptimisticLockingFailureException e) { }

        ProductEntity foundEntity = repository.findById(savedEntity.getId()).get();

        assertEquals(1, (int)foundEntity.getVersion());
        assertEquals("n1", foundEntity.getName());
    }

    private void assertEqualsProduct(ProductEntity expectedEntity, ProductEntity actualEntity) {
        assertEquals(expectedEntity.getId(), actualEntity.getId());
        assertEquals(expectedEntity.getVersion(), actualEntity.getVersion());
        assertEquals(expectedEntity.getProductId(), actualEntity.getProductId());
        assertEquals(expectedEntity.getName(), actualEntity.getName());
        assertEquals(expectedEntity.getWeight(), actualEntity.getWeight());
    }
}
