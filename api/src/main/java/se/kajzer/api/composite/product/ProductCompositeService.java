package se.kajzer.api.composite.product;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

@Api(value = "REST API for composite product information.")
public interface ProductCompositeService {

    /**
     * Sample usage: curl $HOST:$PORT/product-composite/1
     *
     * @param productId
     * @return the composite product info, if found, else null
     */
    @ApiOperation(
            value = "${api.product-composite.get-composite-product.description}",
            notes = "${api.product-composite.get-composite-product.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails. See response message for more information.")
    })
    @GetMapping(
            value = "/product-composite/{productId}",
            produces = "application/json")
    ProductAggregate getCompositeProduct(@PathVariable int productId);

    /**
     * Creates
     *
     * @param body ProductAggregate '{"productId":123,"name":"product 123","weight":123}'
     */
    @ApiOperation(
            value = "${api.common.product-composite.create-composite-product.description}",
            notes = "{api.common.product-composite.create-composite-product.notes}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails. See response message for more information.")
    })
    @PostMapping(
            value = "/product-composite",
            consumes = "application/json")
    void createCompositeProduct(@RequestBody ProductAggregate body);

    @ApiOperation(
            value = "${api.common.product-composite.delete-composite-product.description}",
            notes = "${api.common.product-composite.delete-composite-product.description}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
            @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
            @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fails. See response message for more information.")
    })
    @DeleteMapping(
            value = "product-composite/{productId}",
            produces = "application/json")
    void deleteCompositeProduct(@PathVariable int productId);
}
