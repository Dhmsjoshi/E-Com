package dev.dharam.productservice.dtos;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


public record UpdateCategoryRequestDto (
   @Size(min = 5, max = 200, message = "Description must be between 5 and 200 characters")
   String description
){};
