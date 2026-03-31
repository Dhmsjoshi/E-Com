package dev.dharam.productservice.service.productservice;

import dev.dharam.productservice.dtos.*;
import dev.dharam.productservice.exceptions.ResourceAlreadyExistsException;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import dev.dharam.productservice.mapper.DtoMapper;
import dev.dharam.productservice.models.Category;
import dev.dharam.productservice.models.Product;
import dev.dharam.productservice.repositories.ProductRepository;
import dev.dharam.productservice.repositories.specs.ProductSpecs;
import dev.dharam.productservice.service.categoryservice.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.util.ClassUtils.ifPresent;

@Service("SelfProductService")
@Slf4j
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

    public List<ProductResponseDto> getHomePageProducts(){
        int dayOfWeek = java.time.LocalDate.now().getDayOfWeek().getValue();
        boolean isSaleDay = (dayOfWeek == 2); // Tuesday Test
        Double budgetLimit = 2000.0;

            // Direct stream processing bina extra intermediate variables ke
            return categoryService.getAllCategories().stream()
                    .map(dtoMapper::toCategoryEntity)
                    .map(category -> isSaleDay
                            ? productRepository.findFirstByCategoryIdAndPriceLessThanEqualAndIsDeletedFalseOrderByCreatedAtDesc(category.getId(), budgetLimit)
                            : productRepository.findFirstByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(category))
                    .filter(Optional::isPresent)
                    .map(opt -> ProductResponseDto.from(opt.get()))
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

        // Quantity set karo (Default 0 agar null ho toh)
        product.setQuantity(requestDto.quantity() != null ? requestDto.quantity() : 0);

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


    @Override
    public PagedResponse<ProductResponseDto> getProductsByCategory(
            Long categoryId,
            int pageNum,
            int pageSize,
            String sortBy,
            String direction) {

        categoryService.existsById(categoryId);

        Sort sorting = direction.equalsIgnoreCase("desc")
                ?Sort.by(sortBy).descending()
                :Sort.by(sortBy).ascending();
        Pageable pageable= PageRequest.of(pageNum-1, pageSize, sorting);

        Page<Product>  page = productRepository.findByCategory_IdAndIsDeletedFalse(categoryId,pageable);
        return toPagedResponse(page);
    }

    private PagedResponse<ProductResponseDto> toPagedResponse(Page<Product> page){
        List<ProductResponseDto> dtos= page.getContent().stream()
                .map(ProductResponseDto::from)
                .toList();
        return new PagedResponse<>(dtos,
                                    new PagedResponse.Metadata(
                                            page.getNumber(),
                                            page.getTotalPages(),
                                            page.getTotalElements(),
                                            page.isLast()
                                    )
        );
    }

    @Override
    public PagedResponse<ProductResponseDto> searchProducts(ProductSearchCriteria criteria) {

        categoryService.existsById(criteria.categoryId());

        Sort sorting=criteria.direction().equalsIgnoreCase("desc")
                ?Sort.by(criteria.sortBy()).descending()
                :Sort.by(criteria.sortBy()).ascending();

        Pageable pageable = PageRequest.of(criteria.pageNum(), criteria.pageSize(), sorting);

        Specification<Product> spec = Specification.unrestricted();

        spec = spec.and(ProductSpecs.isNotDeleted())
                .and(ProductSpecs.hasCategory(criteria.categoryId()))
                .and(ProductSpecs.hasTitleLike(criteria.query()))
                .and(ProductSpecs.priceBetween(criteria.minPrice(), criteria.maxPrice()));

        Page<Product> page =  productRepository.findAll(spec, pageable);

        if (page.isEmpty()) {
            throw new ResourceNotFoundException("No products found for the given criteria");
        }

        return toPagedResponse(page);
    }

    @Transactional
    @Override
    public void reduceStock(Long productId, Integer quantityToReduce) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));
        if (product.getQuantity() < quantityToReduce) {
            throw new RuntimeException("Product '" + product.getTitle() + "' is out of stock!");
        }

        product.setQuantity(product.getQuantity() - quantityToReduce);
        //On Save() -> Optimistic Locking
        productRepository.save(product);
        log.info("Stock reduced for product {}. New quantity: {}", productId, product.getQuantity());
    }

    @Transactional
    @Override
    public void increaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found!"));

        product.setQuantity(product.getQuantity() + quantity);
        productRepository.save(product);
        log.info("Stock increased for product {}. New quantity: {}", productId, product.getQuantity());
    }
}
