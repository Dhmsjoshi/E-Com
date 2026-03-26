package dev.dharam.productservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishListController {


    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> getMyWishList(Authentication authentication) {
        String currentUser = authentication.getName();
        return ResponseEntity.ok("This is the wish list of user "+currentUser);
    }

    @PostMapping("/{productId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<String> addToWhishLit(@PathVariable("productId") Long productId, Authentication authentication) {
        return ResponseEntity.ok("Product with id " + productId + " has been added to the whish list");
    }
}
