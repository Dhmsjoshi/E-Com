package dev.dharam.productservice.repositories;

import dev.dharam.productservice.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Override
    Optional<Category> findById(Long aLong);


    Optional<Category> findByName(String categoryName);

    @Override
    List<Category> findAll();

    @Override
    Category save(Category category);

//    @Modifying(clearAutomatically = true)
//    @Query("DELETE FROM Category c WHERE c.id = ?1")
//    int deleteByCategoryId(Long id);


    @Override
    void delete(Category category);
}
