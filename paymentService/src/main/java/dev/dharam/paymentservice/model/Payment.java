package dev.dharam.paymentservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_external_ref", columnList = "externalReferenceId"),
        @Index(name = "idx_order_id", columnList = "orderId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseAuditModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Order ID cannot be null")
    @Column(name = "order_id",  nullable = false)
    private Long orderId;

    @NotNull(message = "Amount cannot be null")
    @Min(value = 1, message = "Amount must be grateer than 0")
    @Column(name = "amount", nullable = false)
    private Long amount;

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private PaymentStatus status;

    //Generic Gateway fields
    @NotBlank(message = "Gateway name is required")
    @Column(name = "gateway_name", nullable = false, length = 50)
    private String gatewayName; // e.g., RAZORPAY

    @Column(name = "external_order_id", length = 100)
    private String externalOrderId;

    @NotBlank(message = "External reference ID is required")
    @Column(name = "external_reference_id", nullable = false, length = 100)
    private String externalReferenceId; // plink_id or session_id

    @Column(name = "transaction_id", length = 100)
    private String transactionId; // pay_id (We get After Success)

    @Column(name = "payment_link_url", length = 500)
    private String paymentLinkUrl;

    @NotBlank(message = "Idempotency key is required")
    @Column(name = "idempotency_key", nullable = false,unique = true, length = 100)
    private String idempotencyKey;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Lob
    @Column(name = "raw_webhook_data", columnDefinition = "TEXT")
    private String rawWebhookData;




}
