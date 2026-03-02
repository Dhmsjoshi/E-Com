package dev.dharam.productservice.controller;

import dev.dharam.productservice.dtos.CategoryDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @GetMapping
    public String getAllCategories(){
        return "Getting all categories.";
    }

    @GetMapping("/{categoryId}")
    public String getCategoryById(@PathVariable("categoryId") Long categoryId){
        return "Getting category by Id: " + categoryId;
    }

    @PostMapping
    public String createCategory(@RequestBody CategoryDto categoryDto){
        return categoryDto.toString();
    }

    @PatchMapping("/{categoryId}")
    public String updateCategory(@RequestBody CategoryDto categoryDto, @PathVariable("categoryId") Long categoryId){
        return categoryDto.toString();
    }

    @DeleteMapping("/{categoryId}")
    public String deleteCategory(@PathVariable("categoryId") Long categoryId){
        return "Deleting category with id: "+categoryId;
    }

}
