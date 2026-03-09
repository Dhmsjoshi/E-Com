package dev.dharam.productservice.dtos;

import java.util.List;

public record PagedResponse<T>(List<T> data, Metadata metadata) {
    public record Metadata(
            int currentPage,
            int totalPages,
            long totalElements,
            boolean isLastPage
    ){}
}
