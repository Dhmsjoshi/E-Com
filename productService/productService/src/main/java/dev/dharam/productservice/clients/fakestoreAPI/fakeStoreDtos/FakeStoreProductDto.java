package dev.dharam.productservice.clients.fakestoreAPI.fakeStoreDtos;

import lombok.Getter;
import lombok.Setter;


public record FakeStoreProductDto (
    Long id,
    String title,
    Double price,
    String description,
    String category,
    String image,
    Integer quantity)
{};
