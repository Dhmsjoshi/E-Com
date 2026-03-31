package dev.dharam.productservice.service.productservice;

import dev.dharam.productservice.dtos.*;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import dev.dharam.productservice.models.Product;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    public List<ProductResponseDto> getAllProducts();
    List<ProductResponseDto> getHomePageProducts();

    public ProductResponseDto getProductById(Long productId);

    public ProductResponseDto createProduct( CreateProductRequestDto requestDto);


    public ProductResponseDto updateProduct( UpdateProductRequestDto requestDto,
                                 Long productId );

    public String deleteProduct( Long productId);
    PagedResponse<ProductResponseDto>getProductsByCategory(Long categoryId,int pageNum,int pageSize,String sortBy,String direction);

    PagedResponse<ProductResponseDto> searchProducts(ProductSearchCriteria criteria);

    void reduceStock(Long productId, Integer quantityToReduce);
    void increaseStock(Long productId, Integer quantity);
}
