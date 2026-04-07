package dev.dharam.paymentservice.client.orderservice;

import dev.dharam.paymentservice.dto.PaymentResultDto;
import dev.dharam.paymentservice.exception.ResourceNotFoundException;
import dev.dharam.paymentservice.exception.ServiceUnavailableException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceClient {

    private final RestTemplate restTemplate;
    private final String ORDER_SERVICE_BASE_URL ="http://orderService/api/v1/orders/";


    public Long getOrderAmount(Long orderId){
        String url =  ORDER_SERVICE_BASE_URL + "{orderId}/payment-amount";
        log.info("Fetching amount from Order Service for Order ID: {}", orderId);

        ResponseEntity<Long> response = requestForEntity(HttpMethod.GET, url, null, Long.class, orderId);
        return Objects.requireNonNull(response.getBody(), "Order Service returned aempty amount");
    }

    public void updatePaymentStatus(Long orderId, boolean isSuccess, String transactionId){
        String url = ORDER_SERVICE_BASE_URL+"{orderId}/payment-status";

        PaymentResultDto paymentResult =new PaymentResultDto(
            isSuccess,
            isSuccess ?"Payment Successful" : "Payment failed",
            transactionId
        );

        requestForEntity(
                HttpMethod.PATCH,
                url,
                paymentResult,
                Void.class,
                orderId
        );
        log.info("Order Service status update request sent successfully for Order ID: {}", orderId);
    }


    private <T> ResponseEntity<T> requestForEntity(HttpMethod httpMethod, String url, @Nullable Object request,
                                                   Class<T> responseType, Object... uriVariables) {
        try {
            RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
            ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);

            return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);

        } catch (HttpClientErrorException.NotFound e) {
            log.error("Order Service: Product not found at URL: {}", url);
            throw new ResourceNotFoundException("Opps! Selected product is not available.");

        } catch (HttpServerErrorException e) {
            log.error("Order Service: Product Service (5xx) issue: {}", e.getResponseBodyAsString());
            throw new ServiceUnavailableException("Product Service is currently down. Please try again later!");

        } catch (RestClientException e) {
            log.error("Order Service: Communication failure: {}", e.getMessage());
            throw new ServiceUnavailableException("Unable to reach Product Service!");
        }
    }


}
