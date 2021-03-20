package se.kajzer.microservices.core.product.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.dao.DuplicateKeyException;
import se.kajzer.api.core.product.Product;
import se.kajzer.api.core.product.ProductService;
import se.kajzer.microservices.core.product.persistence.ProductEntity;
import se.kajzer.microservices.core.product.persistence.ProductRepository;
import se.kajzer.util.exceptions.InvalidInputException;
import se.kajzer.util.exceptions.NotFoundException;
import se.kajzer.util.http.ServiceUtil;


@RestController
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ServiceUtil serviceUtil;
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Autowired
    public ProductServiceImpl(ServiceUtil serviceUtil, ProductRepository repository, ProductMapper mapper) {
        this.serviceUtil = serviceUtil;
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Product getProduct(int productId) {
        if (productId < 1)
            throw new InvalidInputException("Invalid value for product ID: " + productId);

        ProductEntity foundProduct = repository.findByProductId(productId).orElseThrow(() -> new NotFoundException("No product was found with product ID: " + productId));
        Product productResponse = mapper.entityToApi(foundProduct);
        productResponse.setServiceAddress(serviceUtil.getServiceAddress());
        return productResponse;
    }

    @Override
    public Product createProduct(Product body) {
        try {
            ProductEntity productEntity = mapper.apiToEntity(body);
            ProductEntity newEntity = repository.save(productEntity);
            return mapper.entityToApi(newEntity);
        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, product ID: " + body.getProductId());
        }
    }

    @Override
    public void deleteProduct(int productId) {
        if (productId < 1)
            throw new InvalidInputException("Invalid value for product ID:" + productId);

        repository.findByProductId(productId).ifPresent(e -> repository.delete(e));
    }
}