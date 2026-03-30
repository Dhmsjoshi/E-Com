package dev.dharam.productservice.repositories;

import dev.dharam.productservice.models.Category;
import dev.dharam.productservice.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long>, JpaSpecificationExecutor<Product> {

    @Override
    Optional<Product> findById(Long aLong);


    Optional<Product> findByTitle(String title);

    @Override
    List<Product> findAll();

    @Override
    Product save(Product product);

    @Override
    boolean existsById(Long productId);


    @Override
    void delete(Product entity);

     Page<Product> findAll(Pageable pageable);

    Optional<Product> findFirstByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(Category category);

    Page<Product> findByCategory_IdAndIsDeletedFalse(Long categoryId, Pageable pageable);

    Page<Product> findAll(Specification spec, Pageable pageable);

    Optional<Product> findFirstByCategoryIdAndPriceLessThanEqualAndIsDeletedFalseOrderByCreatedAtDesc(Long categoryId, Double maxPrice);

    Optional<Product> findFirstByCategoryIdAndIsDeletedFalseOrderByCreatedAtDesc(Long categoryId);

//    @Modifying
//    @Transactional
//    @Query("UPDATE Product p SET p.quantity = p.quantity - :qty WHERE p.id = :id AND p.quantity >= :qty")
//    int decreaseStock(Long id, Integer qty);

}
