package dev.dharam.productservice.service.productservice;

import dev.dharam.productservice.clients.fakestoreAPI.FakeStoreClient;
import dev.dharam.productservice.dtos.*;
import dev.dharam.productservice.clients.fakestoreAPI.fakeStoreDtos.FakeStoreProductDto;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import dev.dharam.productservice.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
//@Service
public class FakeStoreProductServiceImpl implements ProductService{

    private final FakeStoreClient fakeStoreClient;

    public FakeStoreProductServiceImpl(FakeStoreClient fakeStoreClient) {
        this.fakeStoreClient = fakeStoreClient;
    }


    @Override
    public List<ProductResponseDto> getAllProducts() {
       List<FakeStoreProductDto> fakeStoreProductDtoList = fakeStoreClient.getAllProducts();
        List<ProductResponseDto> products = new ArrayList<>();
        for(FakeStoreProductDto dto: fakeStoreProductDtoList){
            products.add(ProductResponseDto.fromFakeStoreDto(dto));
        }
        return products;
    }

    @Override
    public ProductResponseDto getProductById(Long productId) throws ResourceNotFoundException {
       FakeStoreProductDto fakeStoreProductDto = fakeStoreClient.getProductById(productId);
       return ProductResponseDto.fromFakeStoreDto(fakeStoreProductDto);

    }

    @Override
    public ProductResponseDto createProduct( CreateProductRequestDto requestDto) throws ResourceNotFoundException {
        FakeStoreProductDto fakeStoreProductDto = fakeStoreClient.createProduct(requestDto);
        return ProductResponseDto.fromFakeStoreDto(fakeStoreProductDto);

    }

    @Override
    public ProductResponseDto updateProduct(UpdateProductRequestDto requestDto, Long productId) {
        FakeStoreProductDto fakeStoreProductDto = fakeStoreClient.updateProduct(requestDto,productId);
        return ProductResponseDto.fromFakeStoreDto(fakeStoreProductDto);

    }


    @Override
    public String deleteProduct(Long productId) {
        return fakeStoreClient.deleteProduct(productId);

    }

    @Override
    public List<ProductResponseDto> getHomePageProducts() {
        return List.of();
    }

    @Override
    public PagedResponse<ProductResponseDto> getProductsByCategory(Long categoryId, int pageNum, int pageSize, String sortBy, String direction) {
        return null;
    }

    @Override
    public PagedResponse<ProductResponseDto> searchProducts(ProductSearchCriteria criteria) {
        return null;
    }

    @Override
    public void reduceStock(Long productId, Integer quantityToReduce) {

    }

    @Override
    public void increaseStock(Long productId, Integer quantity) {

    }
}
