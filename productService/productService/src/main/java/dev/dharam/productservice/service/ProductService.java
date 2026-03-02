package dev.dharam.productservice.service;

import dev.dharam.productservice.dtos.CreateProductRequestDto;
import dev.dharam.productservice.dtos.ProductResponseDto;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface ProductService {

    public List<ProductResponseDto> getAllProducts();

    public ProductResponseDto getProductById(Long productId) throws ResourceNotFoundException;

    public ProductResponseDto createProduct( CreateProductRequestDto requestDto)  throws ResourceNotFoundException;


    public ProductResponseDto updateProduct( CreateProductRequestDto createProductRequestDto,
                                 Long productId );

    public ProductResponseDto replaceProduct( CreateProductRequestDto createProductRequestDto,
                                             Long productId );


    public String deleteProduct( Long productId);
}
