package dev.dharam.orderservice.repository;

import dev.dharam.orderservice.model.Order;
import dev.dharam.orderservice.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {


    List<Order> findByUserId(UUID userId);

    @Override
    Optional<Order> findById(Long orderId);
}
