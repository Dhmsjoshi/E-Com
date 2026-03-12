package dev.dharam.productservice.service.categoryservice;

import dev.dharam.productservice.dtos.CategoryResponseDto;
import dev.dharam.productservice.dtos.CreateCategoryRequestDto;
import dev.dharam.productservice.dtos.UpdateCategoryRequestDto;
import dev.dharam.productservice.exceptions.ResourceAlreadyExistsException;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface CategoryService {

    public List<CategoryResponseDto> getAllCategories();


    public CategoryResponseDto getCategoryById( Long categoryId) ;


    public CategoryResponseDto createCategory(CreateCategoryRequestDto requestDto);

    public CategoryResponseDto updateCategory(UpdateCategoryRequestDto requestDto, Long categoryId);;

    public String deleteCategory( Long categoryId);
    void existsById(Long categoryId);
}
