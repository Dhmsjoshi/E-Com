package dev.dharam.cartservice.repository;

import dev.dharam.cartservice.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    @Override
    Optional<Cart> findById(UUID cartUserId);

    @Override
    Cart  save(Cart cart);
}
