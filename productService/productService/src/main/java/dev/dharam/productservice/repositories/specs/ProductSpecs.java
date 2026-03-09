package dev.dharam.productservice.repositories.specs;

import dev.dharam.productservice.models.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecs {
    public static Specification<Product> hasTitleLike(String title){
        return (root, query, cb)->{
            if(title!=null && !title.isBlank())return null;
            return cb.like(cb.lower(root.get("title")), "%"+title.toLowerCase()+"%");
        };
    }

    public static Specification<Product> hasCategory(Long categoryId){
        return (root,query,cb)->{
            if(categoryId == null) return null;
            return cb.equal(root.get("category").get("id"), categoryId);
        };
    }
    public static Specification<Product> priceBetween(Double min, Double max) {
        return (root, query, cb) -> {
            if (min != null && max != null) return cb.between(root.get("price"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("price"), min);
            if (max != null) return cb.lessThanOrEqualTo(root.get("price"), max);
            return null;
        };
    }

    // 4. Soft Delete Filter
    public static Specification<Product> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("isDeleted"), false);
    }
}
