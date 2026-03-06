package dev.dharam.productservice.controller;

import dev.dharam.productservice.dtos.CreateProductRequestDto;
import dev.dharam.productservice.dtos.ProductResponseDto;
import dev.dharam.productservice.dtos.UpdateProductRequestDto;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import dev.dharam.productservice.service.productservice.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(@Qualifier("SelfProductService") ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts(){
        return new ResponseEntity<>(productService.getAllProducts(),HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable("productId") Long productId) throws ResourceNotFoundException {

            ProductResponseDto productDto = productService.getProductById(productId);
            return new ResponseEntity<>(productDto, HttpStatus.OK);


    };

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody CreateProductRequestDto requestDto){
        return new ResponseEntity<>(productService.createProduct(requestDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductResponseDto> updateProduct(@RequestBody UpdateProductRequestDto rquestDto,
                                @PathVariable("productId") Long productId ){
        return new ResponseEntity<>(productService.updateProduct(rquestDto, productId),HttpStatus.OK);
    }

//    @PutMapping("/{productId}")
//    public ProductResponseDto replaceProduct(@RequestBody UpdateProductRequestDto rquestDto,
//                                            @PathVariable("productId") Long productId ){
//        return productService.replaceProduct(rquestDto, productId);
//    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable("productId") Long productId){
        return new ResponseEntity<>(productService.deleteProduct(productId),HttpStatus.OK);
    }


}
