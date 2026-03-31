package dev.dharam.orderservice.client.productservice;

import dev.dharam.orderservice.client.productservice.dto.ProductDetailsDto;
import dev.dharam.orderservice.exception.ResourceNotFoundException;
import dev.dharam.orderservice.exception.ServiceUnavailableException;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {
    private final RestTemplate restTemplate;
    private final String PRODUCT_SERVICE_URL = "http://localhost:8081/products/";

    public ProductDetailsDto  getProductDetails(Long productId) {
        String url = PRODUCT_SERVICE_URL + "{productId}";
        ResponseEntity<ProductDetailsDto> response = requestForEntity(HttpMethod.GET, url,null, ProductDetailsDto.class, productId);
        return response.getBody();
    }

    public void reduceStock(Long productId, Integer quantity) {
        // Endpoint: /products/{id}/reduce-stock?quantity=5
        String url = PRODUCT_SERVICE_URL + "{productId}/reduce-stock?quantity={qty}";
        log.info("Order Service: Requesting to REDUCE stock for Product ID: {} by {}", productId, quantity);

        requestForEntity(HttpMethod.PUT, url, null, Void.class, productId, quantity);

        log.info("Order Service: Stock reduced successfully for Product ID: {}", productId);
    }


    public void increaseStock(Long productId, Integer quantity) {
        // Endpoint: /products/{id}/increase-stock?quantity=5
        String url = PRODUCT_SERVICE_URL + "{productId}/increase-stock?quantity={qty}";
        log.info("Order Service: Requesting to increase stock for Product ID: {} by {}", productId, quantity);

        requestForEntity(HttpMethod.PUT, url, null, Void.class, productId, quantity);
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
