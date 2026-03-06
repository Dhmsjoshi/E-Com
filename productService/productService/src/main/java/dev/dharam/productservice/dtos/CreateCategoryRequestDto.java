package dev.dharam.productservice.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateCategoryRequestDto {
    private String name;
    private String description;
}
