package dev.dharam.productservice.dtos;

import dev.dharam.productservice.models.Category;
import dev.dharam.productservice.models.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public record CategoryResponseDto (
     Long id,
     String categoryName,
     String description){

    public static CategoryResponseDto from(Category category) {
        // Records use the constructor to set all fields at once
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
