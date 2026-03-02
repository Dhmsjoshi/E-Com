package dev.dharam.productservice.service;

import dev.dharam.productservice.dtos.CreateProductRequestDto;
import dev.dharam.productservice.dtos.ProductResponseDto;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;

import java.util.List;

public class ProductServiceImpl implements ProductService{
    @Override
    public List<ProductResponseDto> getAllProducts() {
        return List.of();
    }

    @Override
    public ProductResponseDto getProductById(Long productId)throws ResourceNotFoundException {
        return null;
    }

    @Override
    public ProductResponseDto createProduct( CreateProductRequestDto requestDto)  throws ResourceNotFoundException {
        return null;
    }

    @Override
    public ProductResponseDto updateProduct(CreateProductRequestDto createProductRequestDto, Long productId) {
        return null;
    }
    @Override
    public ProductResponseDto replaceProduct( CreateProductRequestDto createProductRequestDto,
                                              Long productId ){
        return null;
    };

    @Override
    public String deleteProduct(Long productId) {
        return "";
    }
}
