package dev.dharam.productservice.service;

import dev.dharam.productservice.clients.fakestoreAPI.FakeStoreClient;
import dev.dharam.productservice.dtos.CreateProductRequestDto;
import dev.dharam.productservice.clients.fakestoreAPI.fakeStoreDtos.FakeStoreProductDto;
import dev.dharam.productservice.dtos.ProductResponseDto;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
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
            products.add(ProductResponseDto.from(dto));
        }
        return products;
    }

    @Override
    public ProductResponseDto getProductById(Long productId) throws ResourceNotFoundException {
       FakeStoreProductDto fakeStoreProductDto = fakeStoreClient.getProductById(productId);
       return ProductResponseDto.from(fakeStoreProductDto);

    }

    @Override
    public ProductResponseDto createProduct( CreateProductRequestDto requestDto) throws ResourceNotFoundException {
        FakeStoreProductDto fakeStoreProductDto = fakeStoreClient.createProduct(requestDto);
        return ProductResponseDto.from(fakeStoreProductDto);

    }

    @Override
    public ProductResponseDto updateProduct(CreateProductRequestDto createProductRequestDto, Long productId) {
        FakeStoreProductDto fakeStoreProductDto = fakeStoreClient.updateProduct(createProductRequestDto,productId);
        return ProductResponseDto.from(fakeStoreProductDto);


    }

    @Override
    public ProductResponseDto replaceProduct( CreateProductRequestDto createProductRequestDto,
                                              Long productId ){
        FakeStoreProductDto fakeStoreProductDto = fakeStoreClient.replaceProduct(createProductRequestDto,productId);
        return ProductResponseDto.from(fakeStoreProductDto);
    };

    @Override
    public String deleteProduct(Long productId) {
        return fakeStoreClient.deleteProduct(productId);

    }
}
