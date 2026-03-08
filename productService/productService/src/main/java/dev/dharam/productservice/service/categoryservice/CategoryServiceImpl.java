package dev.dharam.productservice.service.categoryservice;

import dev.dharam.productservice.dtos.CategoryResponseDto;
import dev.dharam.productservice.dtos.CreateCategoryRequestDto;
import dev.dharam.productservice.dtos.UpdateCategoryRequestDto;
import dev.dharam.productservice.exceptions.ResourceAlreadyExistsException;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import dev.dharam.productservice.models.Category;
import dev.dharam.productservice.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService{
    private final CategoryRepository categoryRepository;
    
    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<CategoryResponseDto> getAllCategories(){
        List<Category> categoryList = categoryRepository.findAll();
        List<CategoryResponseDto> categoryResponseDtoList = new ArrayList<>();
        if(categoryList.isEmpty()){
            throw new ResourceNotFoundException("Category Not Found");
        }
        for(Category category : categoryList){
            categoryResponseDtoList.add(CategoryResponseDto.from(category));
        }
        return categoryResponseDtoList;
    }

    @Override
    public CategoryResponseDto getCategoryById(Long categoryId){
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                ()-> new ResourceNotFoundException("Category with id:"+categoryId +" not found!")
        );
        return CategoryResponseDto.from(category);
    }

    @Override
    @Transactional
    public CategoryResponseDto createCategory(CreateCategoryRequestDto requestDto){
        categoryRepository.findByName(requestDto.name()).ifPresent(
                c-> { throw new ResourceAlreadyExistsException(
                        "Category with name: "+requestDto.name()+" already exists!");
                });
        Category category = new Category();
        category.setName(requestDto.name());
        category.setDescription(requestDto.description());
        Category savedCategory = categoryRepository.save(category);
        return CategoryResponseDto.from(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(UpdateCategoryRequestDto requestDto, Long categoryId){
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                ()-> new ResourceNotFoundException("Category with id:"+categoryId +" not found!")
        );

        if(requestDto.description() != null)category.setDescription(requestDto.description());
//        Category updatedCategory = categoryRepository.save(category);
        return CategoryResponseDto.from(category);
    }


    @Override
    @Transactional
    public String deleteCategory(Long categoryId){
        Category category = categoryRepository.findById(categoryId).orElseThrow(
                ()-> new ResourceNotFoundException("Category with id:"+categoryId +" not found!")
        );
        categoryRepository.delete(category);
        return "Category with id:"+categoryId +" deleted successfully!";
    }
}
