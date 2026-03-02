package dev.dharam.productservice.controller;

import dev.dharam.productservice.dtos.CreateProductRequestDto;
import dev.dharam.productservice.dtos.ProductResponseDto;
import dev.dharam.productservice.exceptions.ExceptionResponseDto;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import dev.dharam.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<ProductResponseDto> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("productId") Long productId) throws ResourceNotFoundException {

            ProductResponseDto productDto = productService.getProductById(productId);
            return new ResponseEntity<>(productDto, HttpStatus.OK);


    };

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody CreateProductRequestDto requestDto){
        try{
            ProductResponseDto productDto = productService.createProduct(requestDto);
            return new ResponseEntity<>(productDto, HttpStatus.OK);
        }catch (ResourceNotFoundException e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{productId}")
    public ProductResponseDto updateProduct(@RequestBody CreateProductRequestDto createProductRequestDto,
                                @PathVariable("productId") Long productId ){
        return productService.updateProduct(createProductRequestDto, productId);
    }

    @PutMapping("/{productId}")
    public ProductResponseDto replaceProduct(@RequestBody CreateProductRequestDto createProductRequestDto,
                                            @PathVariable("productId") Long productId ){
        return productService.replaceProduct(createProductRequestDto, productId);
    }

    @DeleteMapping("/{productId}")
    public String deleteProduct(@PathVariable("productId") Long productId){
        return "Product deleted with id: "+productId;
    }


}
