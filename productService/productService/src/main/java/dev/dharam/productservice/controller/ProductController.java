package dev.dharam.productservice.controller;

import dev.dharam.productservice.dtos.CreateProductRequestDto;
import dev.dharam.productservice.dtos.ProductResponseDto;
import dev.dharam.productservice.dtos.UpdateProductRequestDto;
import dev.dharam.productservice.exceptions.ErrorResponseDto;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import dev.dharam.productservice.service.productservice.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@Validated
@Tag(name = "Product Service", description = "Endpoints for managing the product catalog")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(@Qualifier("SelfProductService") ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a complete list of products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of products")
    })
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(){
        return new ResponseEntity<>(productService.getAllProducts(),HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get product by ID", description = "Fetches detailed information about a specific product using its unique database ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found and returned"),
            @ApiResponse(responseCode = "404", description = "Product not found with the provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID format (must be a positive number)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ProductResponseDto> getProductById(
            @PathVariable("productId")
            @NotNull(message = "ID is required")
            @Positive(message = "ID must be a positive number")
            Long productId) {

            ProductResponseDto productDto = productService.getProductById(productId);
            return new ResponseEntity<>(productDto, HttpStatus.OK);


    };

    @PostMapping
    @Operation(summary = "Create a new product", description = "Validates the input and saves a new product record linked to a valid Category ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed (check 'details' for field errors)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: Product with same details already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody @Valid CreateProductRequestDto requestDto){
        return new ResponseEntity<>(productService.createProduct(requestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{productId}")
    @Operation(summary = "Partially update a product", description = "Updates only the fields provided in the request body for an existing product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data or ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<ProductResponseDto> updateProduct(
            @RequestBody @Valid UpdateProductRequestDto requestDto,
            @PathVariable("productId")
            @NotNull(message = "ID is required")
            @Positive(message = "ID must be a positive number")
            Long productId ){
        return new ResponseEntity<>(productService.updateProduct(requestDto, productId),HttpStatus.OK);
    }

//    @PutMapping("/{productId}")
//    public ProductResponseDto replaceProduct(@RequestBody UpdateProductRequestDto requestDto,
//                                            @PathVariable("productId") Long productId ){
//        return productService.replaceProduct(requestDto, productId);
//    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete a product", description = "Permanently removes a product from the database catalog based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<String> deleteProduct(
            @PathVariable("productId")
            @NotNull(message = "ID is required")
            @Positive(message = "ID must be a positive number")
            Long productId){
        return new ResponseEntity<>(productService.deleteProduct(productId),HttpStatus.OK);
    }


}
