package dev.dharam.paymentservice.repository;

import dev.dharam.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Override
     Payment save(Payment payment);


    Optional<Payment> findByExternalOrderId(String externalOrderId);
}
