package dev.dharam.productservice.dtos;

import dev.dharam.productservice.clients.fakestoreAPI.fakeStoreDtos.FakeStoreProductDto;
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

    public static ProductResponseDto from(FakeStoreProductDto fakeStoreProductDto){
        ProductResponseDto productResponseDto = new ProductResponseDto();
        productResponseDto.setId(fakeStoreProductDto.getId());
        productResponseDto.setTitle(fakeStoreProductDto.getTitle());
        productResponseDto.setPrice(fakeStoreProductDto.getPrice());
        productResponseDto.setDescription(fakeStoreProductDto.getDescription());
        productResponseDto.setCategory(fakeStoreProductDto.getCategory());
        productResponseDto.setImageUrl(fakeStoreProductDto.getImage());

        return  productResponseDto;
    }
}
