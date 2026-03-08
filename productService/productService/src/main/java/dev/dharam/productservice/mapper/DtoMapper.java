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
        category.setId(categoryResponseDto.id());
        category.setDescription(categoryResponseDto.description());
        return category;
    }

    public Product toProductEntity(ProductResponseDto productResponseDto){
        Product product = new Product();
        product.setImageUrl(productResponseDto.imageUrl());
        product.setDescription(productResponseDto.description());
        product.setPrice(productResponseDto.price());
        return product;

    }
}
