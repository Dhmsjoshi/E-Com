package dev.dharam.productservice.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@ToString
public class CreateProductRequestDto {
    private String title;
    private double price;
    private String description;
    @NotNull
    private Long categoryId;
    private String imageUrl;
}
