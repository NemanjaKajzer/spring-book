package se.kajzer.microservices.core.product;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import se.kajzer.api.core.product.Product;
import se.kajzer.microservices.core.product.persistence.ProductRepository;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, properties = {"spring.data.mongodb.port: 0"})
public class ProductServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    ProductRepository repository;

    @Before
    public void setupDb() {
        repository.deleteAll();
    }

    private int productId = 1;
    private int productNotFoundId = 12;

    @Test
    public void getProductByIdTest() {
        postAndVerifyProduct(productId, OK);
        assertTrue(repository.findByProductId(productId).isPresent());

        getAndVerifyProduct(productId, OK).jsonPath("$.productId").isEqualTo(productId);
    }

    @Test
    public void getProductInvalidParameterString() {
        getAndVerifyProduct("/no-integer", BAD_REQUEST)
                .jsonPath("$.path").isEqualTo("/product/no-integer");
    }

    @Test
    public void getProductNotFound() {

        int productIdNotFound = 13;
        getAndVerifyProduct(productIdNotFound, NOT_FOUND)
                .jsonPath("$.path").isEqualTo("/product/" + productIdNotFound);
    }

    @Test
    public void duplicateError() {
        postAndVerifyProduct(productId, OK);

        assertTrue(repository.findByProductId(productId).isPresent());

        postAndVerifyProduct(productId, UNPROCESSABLE_ENTITY)
                .jsonPath("$.path").isEqualTo("/product");
    }

    @Test
    public void deleteProductTest() {
        postAndVerifyProduct(productId, OK);
        assertTrue(repository.findByProductId(productId).isPresent());

        deleteAndVerifyProduct(productId, OK);
        assertFalse(repository.findByProductId(productId).isPresent());
    }

    //just adds "/" in front of productId
    private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
        return getAndVerifyProduct("/" + productId, expectedStatus);
    }

    private WebTestClient.BodyContentSpec getAndVerifyProduct(String productIdPath, HttpStatus expectedStatus) {
        return client.get()
                .uri("/product" + productIdPath)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec postAndVerifyProduct(int productId, HttpStatus expectedStatus) {
        Product product = new Product(productId, "Name" + productId, productId, "SA");

        return client.post()
                .uri("/product")
                .body(just(product), Product.class)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectHeader().contentType(APPLICATION_JSON)
                .expectBody();
    }

    private WebTestClient.BodyContentSpec deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
        return client.delete()
                .uri("/product/" + productId)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody();
    }
}