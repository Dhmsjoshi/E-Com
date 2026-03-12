package dev.dharam.productservice.repositories.specs;

import dev.dharam.productservice.models.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {
    public static Specification<Product> hasTitleLike(String title){
        return (product, query, cb)->{
            if(title!=null && !title.isBlank())return null;
            return cb.like(cb.lower(product.get("title")), "%"+title.toLowerCase()+"%");
        };
    }

    public static Specification<Product> hasCategory(Long categoryId){
        return (product,query,cb)->{
            if(categoryId == null) return null;
            return cb.equal(product.get("category").get("id"), categoryId);
        };
    }
    public static Specification<Product> priceBetween(Double min, Double max) {
        return (product, query, cb) -> {
            if (min != null && max != null) return cb.between(product.get("price"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(product.get("price"), min);
            if (max != null) return cb.lessThanOrEqualTo(product.get("price"), max);
            return null;
        };
    }

    // 4. Soft Delete Filter
    public static Specification<Product> isNotDeleted() {
        return (product, query, cb) -> cb.equal(product.get("isDeleted"), false);
    }
}
