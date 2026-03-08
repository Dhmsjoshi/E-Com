package dev.dharam.productservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


public record CreateCategoryRequestDto (
    @NotBlank(message = "Name can not be empty!")
    @Size(min = 3, max = 20, message = "Name must be between 3 and 20 characters")
    String name,

    @NotBlank(message = "Description can not be empty!")
    @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
    String description){
};
