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

import static org.springframework.data.util.ClassUtils.ifPresent;

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
        return productRepository.findAll().stream().map(ProductResponseDto::from)
                .toList();
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
    @Transactional
    public ProductResponseDto createProduct( CreateProductRequestDto requestDto){

        productRepository.findByTitle(requestDto.title()).ifPresent(
                product -> {
                    throw new ResourceAlreadyExistsException("Product with title " + requestDto.title() + " already exists");
                }
        );

        CategoryResponseDto categoryResponseDto = categoryService.getCategoryById(requestDto.categoryId());
        Category productCategory = dtoMapper.toCategoryEntity(categoryResponseDto);

        Product product = new Product();
        product.setTitle(requestDto.title());
        product.setDescription(requestDto.description());
        product.setPrice(requestDto.price());
        product.setCategory(productCategory);
        product.setImageUrl(requestDto.imageUrl());

        Product savedProduct = productRepository.save(product);

        return ProductResponseDto.from(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDto updateProduct(UpdateProductRequestDto requestDto,
                                            Long productId) {
        Product product= productRepository.findById(productId).orElseThrow(
                ()->  new ResourceNotFoundException("Product with id: "+productId+" not found!")
        );
        if(requestDto.description() != null)product.setDescription(requestDto.description());
        if(requestDto.price() != null && requestDto.price() >= 0)product.setPrice(requestDto.price());
        if(requestDto.imageUrl() != null)product.setImageUrl(requestDto.imageUrl());
//        Product updatedProduct =productRepository.save(product);

        return ProductResponseDto.from(product);
    }


    @Override
    @Transactional
    public String deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with ID: " + productId + " not found!"));

        productRepository.delete(product);

        return "Product with ID: " + productId + " deleted successfully!";
    }
}
