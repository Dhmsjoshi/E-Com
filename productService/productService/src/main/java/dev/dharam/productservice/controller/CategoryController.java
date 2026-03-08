package dev.dharam.productservice.controller;

import dev.dharam.productservice.dtos.CategoryResponseDto;
import dev.dharam.productservice.dtos.CreateCategoryRequestDto;
import dev.dharam.productservice.dtos.UpdateCategoryRequestDto;
import dev.dharam.productservice.exceptions.ErrorResponseDto;
import dev.dharam.productservice.exceptions.ResourceAlreadyExistsException;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import dev.dharam.productservice.service.categoryservice.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Validated
@Tag(name = "Category Service", description = "Endpoints for managing the different categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Get all categories", description = "Retrieves a complete list of categories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of categories")
    })
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories()
           {
        return new ResponseEntity<>(categoryService.getAllCategories(), HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by ID", description = "Fetches detailed information about a specific category using its unique database ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category found and returned"),
            @ApiResponse(responseCode = "404", description = "Category not found with the provided ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID format (must be a positive number)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public  ResponseEntity<CategoryResponseDto> getCategoryById(
            @PathVariable("categoryId")
            @NotNull(message = "ID is required")
            @Positive(message = "ID must be a positive number")
            Long categoryId)
             {
        return new ResponseEntity<>(categoryService.getCategoryById(categoryId), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a new category", description = "Validates the input and saves a new category record ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category successfully created"),
            @ApiResponse(responseCode = "400", description = "Validation failed (check 'details' for field errors)",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "Conflict: Category with same details already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<CategoryResponseDto> createCategory(@RequestBody @Valid CreateCategoryRequestDto requestDto)
             {
        return  new ResponseEntity<>(categoryService.createCategory(requestDto), HttpStatus.OK);
    }

    @PatchMapping("/{categoryId}")
    @Operation(summary = "Partially update a category", description = "Updates only the fields provided in the request body for an existing category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid update data or ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<CategoryResponseDto> updateCategory(
            @RequestBody @Valid UpdateCategoryRequestDto requestDto,
            @PathVariable("categoryId")
            @NotNull(message = "ID is required")
            @Positive(message = "ID must be a positive number")
            Long categoryId)
           {
        return new ResponseEntity<>(categoryService.updateCategory(requestDto,categoryId),HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete a category", description = "Permanently removes a category from the database catalog based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid ID format",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<String> deleteCategory(
            @PathVariable("categoryId")
            @NotNull(message = "ID is required")
            @Positive(message = "ID must be a positive number")
            Long categoryId)
            {
        return new ResponseEntity<>(categoryService.deleteCategory(categoryId),HttpStatus.OK);
    }

}
