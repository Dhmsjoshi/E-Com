package dev.dharam.productservice.controller;

import dev.dharam.productservice.dtos.CategoryResponseDto;
import dev.dharam.productservice.dtos.CreateCategoryRequestDto;
import dev.dharam.productservice.dtos.UpdateCategoryRequestDto;
import dev.dharam.productservice.exceptions.ResourceAlreadyExistsException;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import dev.dharam.productservice.service.categoryservice.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories()
           {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public  ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable("categoryId") Long categoryId)
             {
        return new ResponseEntity<>(categoryService.getCategoryById(categoryId), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody CreateCategoryRequestDto requestDto)
             {
        return  new ResponseEntity<>(categoryService.createCategory(requestDto), HttpStatus.OK);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@RequestBody UpdateCategoryRequestDto requestDto, @PathVariable("categoryId") Long categoryId)
           {
        return new ResponseEntity<>(categoryService.updateCategory(requestDto,categoryId),HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(@PathVariable("categoryId") Long categoryId)
            {
        return new ResponseEntity<>(categoryService.deleteCategory(categoryId),HttpStatus.OK);
    }

}
