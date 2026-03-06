package dev.dharam.productservice.dtos;

import dev.dharam.productservice.models.Category;
import dev.dharam.productservice.models.Product;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryResponseDto {
    private Long id;
    private String categoryName;
    private String description;

    public static CategoryResponseDto from(Category category) {
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
        categoryResponseDto.setId(category.getId());
        categoryResponseDto.setCategoryName(category.getName());
        categoryResponseDto.setDescription(category.getDescription());
        return categoryResponseDto;
    }
}
