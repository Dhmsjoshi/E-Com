package dev.dharam.productservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProductSearchCriteria(
        @NotBlank(message = "Search query cannot be empty") String query,
        @NotNull(message = "Category ID is required") Long categoryId,
        Double minPrice,
        Double maxPrice,
        Integer pageNum,    // Use Integer here to check for null
        Integer pageSize,
        String sortBy,
        String direction

) {

    public ProductSearchCriteria{
        if(pageNum ==0 || pageNum <0) pageNum = 0;
        if(pageSize ==0 || pageSize <0) pageSize = 10;
        if(sortBy ==null || sortBy.isEmpty()) sortBy = "price";
        if(direction ==null || direction.isEmpty()) direction = "asc";
        if(minPrice == null || minPrice < 0) minPrice = 0.0;
    }
}
