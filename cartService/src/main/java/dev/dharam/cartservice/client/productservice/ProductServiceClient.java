package dev.dharam.cartservice.client.productservice;

import dev.dharam.cartservice.client.productservice.dto.ProductDetailsDto;
import dev.dharam.cartservice.exceptions.ResourceNotFoundException;
import dev.dharam.cartservice.exceptions.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {
    private final RestTemplate restTemplate;
    private final String PRODUCT_SERVICE_URL = "http://productService/products/";


    public ProductDetailsDto getProductDetails(Long productId) {

        String url = PRODUCT_SERVICE_URL + productId;
        ResponseEntity<ProductDetailsDto> response = requestForEntity(HttpMethod.GET, url,null ,ProductDetailsDto.class);
        return response.getBody();
    }


    private <T> ResponseEntity<T> requestForEntity(HttpMethod httpMethod, String url, @Nullable Object request,
                                                   Class<T> responseType, Object... uriVariables) {
        try {
            RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
            ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);

            return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);

        } catch (HttpClientErrorException.NotFound e) {
            // user-friendly exception
            log.error("Product not found at URL: {}", url);
            throw new ResourceNotFoundException("Opps! Product is not available!.");

        } catch (HttpServerErrorException e) {
            // Server side error (5xx)
            log.error("Product Service is having issues: {}", e.getResponseBodyAsString());
            throw new ServiceUnavailableException("Something went wrong. Please try again later!");

        } catch (RestClientException e) {
            // Timeout, Connection Refused, etc.
            log.error("Communication failure with Product Service: {}", e.getMessage());
            throw new ServiceUnavailableException("Something went wrong!");
        }
    }

}
