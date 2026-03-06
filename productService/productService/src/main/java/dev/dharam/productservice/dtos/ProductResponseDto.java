package dev.dharam.productservice.dtos;

import dev.dharam.productservice.clients.fakestoreAPI.fakeStoreDtos.FakeStoreProductDto;
import dev.dharam.productservice.models.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductResponseDto {
    private long id;
    private String title;
    private double price;
    private String description;
    private String category;
    private String imageUrl;

    public static ProductResponseDto fromFakeStoreDto(FakeStoreProductDto fakeStoreProductDto){
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(fakeStoreProductDto.getId());
        productResponseDto.setTitle(fakeStoreProductDto.getTitle());
        productResponseDto.setPrice(fakeStoreProductDto.getPrice());
        productResponseDto.setDescription(fakeStoreProductDto.getDescription());
        productResponseDto.setCategory(fakeStoreProductDto.getCategory());
        productResponseDto.setImageUrl(fakeStoreProductDto.getImage());

        return  productResponseDto;
    }

    public static ProductResponseDto from(Product product){
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(product.getId());
        productResponseDto.setTitle(product.getTitle());
        productResponseDto.setPrice(product.getPrice());
        productResponseDto.setDescription(product.getDescription());
        productResponseDto.setCategory(product.getCategory().getName());
        productResponseDto.setImageUrl(product.getImageUrl());
        return  productResponseDto;

    }
}
