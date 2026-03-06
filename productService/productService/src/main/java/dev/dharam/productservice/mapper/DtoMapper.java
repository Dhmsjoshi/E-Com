package dev.dharam.productservice.mapper;

import dev.dharam.productservice.dtos.CategoryResponseDto;
import dev.dharam.productservice.dtos.ProductResponseDto;
import dev.dharam.productservice.models.Category;
import dev.dharam.productservice.models.Product;
import org.springframework.stereotype.Component;

@Component
public class DtoMapper {

    public Category toCategoryEntity(CategoryResponseDto categoryResponseDto) {
        Category category = new Category();
        category.setId(categoryResponseDto.getId());
        category.setDescription(categoryResponseDto.getDescription());
        return category;
    }

    public Product toProductEntity(ProductResponseDto productResponseDto){
        Product product = new Product();
        product.setImageUrl(productResponseDto.getImageUrl());
        product.setDescription(productResponseDto.getDescription());
        product.setPrice(productResponseDto.getPrice());
        return product;

    }
}
