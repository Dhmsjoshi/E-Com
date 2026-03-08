package dev.dharam.productservice.service.productservice;

import dev.dharam.productservice.dtos.CreateProductRequestDto;
import dev.dharam.productservice.dtos.ProductResponseDto;
import dev.dharam.productservice.dtos.UpdateProductRequestDto;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface ProductService {

    public List<ProductResponseDto> getAllProducts();

    public ProductResponseDto getProductById(Long productId);

    public ProductResponseDto createProduct( CreateProductRequestDto requestDto);


    public ProductResponseDto updateProduct( UpdateProductRequestDto requestDto,
                                 Long productId );

    public String deleteProduct( Long productId);
}
