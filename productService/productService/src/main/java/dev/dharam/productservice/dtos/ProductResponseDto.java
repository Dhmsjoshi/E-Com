package dev.dharam.productservice.dtos;

import dev.dharam.productservice.clients.fakestoreAPI.fakeStoreDtos.FakeStoreProductDto;
import dev.dharam.productservice.models.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


public record ProductResponseDto(
        Long id,
        String title,
        Double price,
        String description,
        String category,
        String imageUrl
) {
    // Factory method for FakeStore API
    public static ProductResponseDto fromFakeStoreDto(FakeStoreProductDto fakeStoreProductDto) {
        return new ProductResponseDto(
                fakeStoreProductDto.id(),
                fakeStoreProductDto.title(),
                fakeStoreProductDto.price(),
                fakeStoreProductDto.description(),
                fakeStoreProductDto.category(),
                fakeStoreProductDto.image()
        );
    }

    // Factory method for your Internal Entity
    public static ProductResponseDto from(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getTitle(),
                product.getPrice(),
                product.getDescription(),
                // Assuming your Category entity is not null here
                product.getCategory() != null ? product.getCategory().getName() : "Uncategorized",
                product.getImageUrl()
        );
    }
}
