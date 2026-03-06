package dev.dharam.productservice.service.productservice;

import dev.dharam.productservice.dtos.CategoryResponseDto;
import dev.dharam.productservice.dtos.CreateProductRequestDto;
import dev.dharam.productservice.dtos.ProductResponseDto;
import dev.dharam.productservice.dtos.UpdateProductRequestDto;
import dev.dharam.productservice.exceptions.ResourceAlreadyExistsException;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import dev.dharam.productservice.mapper.DtoMapper;
import dev.dharam.productservice.models.Category;
import dev.dharam.productservice.models.Product;
import dev.dharam.productservice.repositories.ProductRepository;
import dev.dharam.productservice.service.categoryservice.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
@Service("SelfProductService")
public class SelfProductServiceImpl implements ProductService{

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final DtoMapper dtoMapper;

    @Autowired
    public SelfProductServiceImpl(ProductRepository productRepository,
                                  CategoryService categoryService,
                                  DtoMapper dtoMapper) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponseDto> productResponseDtoList = new ArrayList<>();
        for(Product product : products){
            productResponseDtoList.add(ProductResponseDto.from(product));
        }

        return productResponseDtoList;
    }

    @Override
    public ProductResponseDto getProductById(Long productId)
            throws ResourceNotFoundException {
        Product product= productRepository.findById(productId).orElseThrow(
                ()-> new ResourceNotFoundException("Product with id: "+productId+" not found!")
        );

        return ProductResponseDto.from(product);
    }

    @Override
    public ProductResponseDto createProduct( CreateProductRequestDto requestDto){
        if(requestDto.getCategoryId() == null){
            throw new ResourceNotFoundException("Category id is null");
        }
        productRepository.findByTitle(requestDto.getTitle()).ifPresent(
                product -> {
                    throw new ResourceAlreadyExistsException("Product with title " + requestDto.getTitle().toUpperCase() + " already exists");
                }
        );

        CategoryResponseDto categoryResponseDto = categoryService.getCategoryById(requestDto.getCategoryId());
        Category productCategory = dtoMapper.toCategoryEntity(categoryResponseDto);

        Product product = new Product();
        product.setTitle(requestDto.getTitle());
        product.setDescription(requestDto.getDescription());
        product.setPrice(requestDto.getPrice());
        product.setCategory(productCategory);
        product.setImageUrl(requestDto.getImageUrl());

        Product savedProduct = productRepository.save(product);

        return ProductResponseDto.from(savedProduct);
    }

    @Override
    public ProductResponseDto updateProduct(UpdateProductRequestDto requestDto,
                                            Long productId) {
        Product product= productRepository.findById(productId).orElseThrow(
                ()->  new ResourceNotFoundException("Product with id: "+productId+" not found!")
        );
        if(requestDto.getDescription() != null)product.setDescription(requestDto.getDescription());
        if(requestDto.getPrice() != 0.00)product.setPrice(requestDto.getPrice());
        if(requestDto.getImageUrl() != null)product.setImageUrl(requestDto.getImageUrl());
        Product updatedProduct =productRepository.save(product);

        return ProductResponseDto.from(updatedProduct);
    }
//    @Override
//    public ProductResponseDto replaceProduct( UpdateProductRequestDto requestDto,
//                                              Long productId ){
//
//        return null;
//    };


    @Override
    public String deleteProduct(Long productId) {
       if(!productRepository.existsById(productId)){
           throw new ResourceNotFoundException("Product with id: "+productId+" not found!");
       }
       productRepository.deleteById(productId);
       if(!productRepository.existsById(productId)){
           return "Product with id: "+productId+" deleted successfully!";
       }
       return "Could not delete!";
    }
}
