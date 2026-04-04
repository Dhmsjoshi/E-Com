package dev.dharam.paymentservice.service;

import dev.dharam.paymentservice.adapter.PaymentGatewayAdepter;
import dev.dharam.paymentservice.client.orderservice.OrderServiceClient;
import dev.dharam.paymentservice.model.Payment;
import dev.dharam.paymentservice.model.PaymentStatus;
import dev.dharam.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReconciliationService {

    private final PaymentRepository paymentRepository;
    private final OrderServiceClient orderServiceClient;
    private final List<PaymentGatewayAdepter> adapters;

    @Scheduled(cron = "0 0/15 * * * *" )
    public void reconcilePayments(){
        log.info("Reconciliation Job Started...");

        //1.Fetch all pending payments from DB
        List<Payment> pendingPayments = paymentRepository.findAllByStatus(PaymentStatus.PENDING);
        for (Payment payment : pendingPayments) {
            try{
                //2.find appropriate adapter
                PaymentGatewayAdepter adepter = adapters
                        .stream()
                        .filter(a->a.getProviderName().toString().equals(payment.getGatewayName()))
                        .findFirst()
                        .orElseThrow(()->new RuntimeException("Payment Gateway Not Found"));

                //3.ask adapter current status from Gateway
                PaymentStatus actualGatewayStatus = adepter.getStatus(payment.getExternalOrderId());

                // 4. CALL THE CONFLICT RESOLVER
                resolveStatusConflict(payment, actualGatewayStatus);
            }catch (Exception e){
                log.error("Reconciliation failed for Payment ID {}: {} ",payment.getId(),e.getMessage());
            }
        }

    }


    private void handleSuccess(Payment payment){
        payment.setStatus(PaymentStatus.SUCCESS);
        paymentRepository.save(payment);

        //give update to OrderSevice
        orderServiceClient.updatePaymentStatus(payment.getOrderId(), true, payment.getTransactionId());
        log.info("Reconciliation: Payment {} marked as SUCCESS", payment.getId());
    }

    private void handleFailure(Payment payment){
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        log.info("Reconciliation: Payment {} marked as FAILED", payment.getId());
    }

    private void resolveStatusConflict(Payment dbPayment, PaymentStatus gatewayStatus){
        PaymentStatus currentDbStatus = dbPayment.getStatus();

        //CASE-1 : GATEWAY SAYS SUCCESS
        if(gatewayStatus == PaymentStatus.SUCCESS){
            if(currentDbStatus == PaymentStatus.PENDING){
                handleSuccess(dbPayment);
            } else if (currentDbStatus == PaymentStatus.FAILED) {
                log.error("CRITICAL: Payment {} is SUCCESS in Gateway but FAILED in DB. Triggering Refund flow.", dbPayment.getId());
                dbPayment.setStatus(PaymentStatus.REFUND);
                paymentRepository.save(dbPayment);
            }
        }
        //CASE-2 : GATEWAY SAYS FAILURE
        else if (gatewayStatus == PaymentStatus.FAILED) {
            if (currentDbStatus == PaymentStatus.PENDING) {
                dbPayment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(dbPayment);
            }
            else if (currentDbStatus == PaymentStatus.SUCCESS) {
                log.error("CRITICAL ALERT: Payment {} is FAILED in Gateway but SUCCESS in DB. Cancelling Order!", dbPayment.getId());
                dbPayment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(dbPayment);
                // Reverse the order in Order Service
                orderServiceClient.updatePaymentStatus(dbPayment.getOrderId(), false, "CANCELLED_BY_RECON");
            }
        }
    }

}
