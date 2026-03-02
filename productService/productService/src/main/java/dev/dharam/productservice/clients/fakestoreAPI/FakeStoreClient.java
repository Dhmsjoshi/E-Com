package dev.dharam.productservice.clients.fakestoreAPI;

import dev.dharam.productservice.dtos.CreateProductRequestDto;
import dev.dharam.productservice.clients.fakestoreAPI.fakeStoreDtos.FakeStoreProductDto;
import dev.dharam.productservice.exceptions.ResourceNotFoundException;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class FakeStoreClient {
    private String FAKE_STORE_PRODUCT_URL = "https://fakestoreapi.com/products";
    private RestTemplateBuilder restTemplateBuilder;



    public FakeStoreClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    public <T> ResponseEntity<T> requestForEntity(HttpMethod httpMethod, String url, @Nullable Object request,
                                                  Class<T> responseType, Object... uriVariables) throws RestClientException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        RequestCallback requestCallback = restTemplate.httpEntityCallback(request, responseType);
        ResponseExtractor<ResponseEntity<T>> responseExtractor = restTemplate.responseEntityExtractor(responseType);
        return restTemplate.execute(url, httpMethod, requestCallback, responseExtractor, uriVariables);
    }



    public List<FakeStoreProductDto> getAllProducts() {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreProductDto[]> responseList = restTemplate.getForEntity(FAKE_STORE_PRODUCT_URL, FakeStoreProductDto[].class);
        List<FakeStoreProductDto> fakeStoreProductDtos = new ArrayList<>();
        for(FakeStoreProductDto dto: responseList.getBody()){
            fakeStoreProductDtos.add(dto);
        }
        return fakeStoreProductDtos;
    }


    public FakeStoreProductDto getProductById(Long productId) throws ResourceNotFoundException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreProductDto> fakeStoreProductDtoResponse =
                restTemplate.getForEntity(FAKE_STORE_PRODUCT_URL+"/{id}",
                        FakeStoreProductDto.class,
                        productId);
        if(fakeStoreProductDtoResponse.getStatusCode().is2xxSuccessful() && fakeStoreProductDtoResponse.getBody() != null){
           return fakeStoreProductDtoResponse.getBody();
        }else {
            throw new ResourceNotFoundException("Product with ID: "+productId+" not found!");
        }

    }


    public FakeStoreProductDto createProduct( CreateProductRequestDto requestDto) throws ResourceNotFoundException {
        RestTemplate restTemplate = restTemplateBuilder.build();
        ResponseEntity<FakeStoreProductDto> fakeStoreProductDtoResponse=
                restTemplate.postForEntity(FAKE_STORE_PRODUCT_URL,requestDto, FakeStoreProductDto.class);

        if(fakeStoreProductDtoResponse.getStatusCode().is2xxSuccessful()){
            return fakeStoreProductDtoResponse.getBody();
        }else{
            throw new ResourceNotFoundException("Invalid Arguments!");
        }

    }


    public FakeStoreProductDto updateProduct(CreateProductRequestDto createProductRequestDto, Long productId) {
        ResponseEntity<FakeStoreProductDto> response = requestForEntity(
                HttpMethod.PATCH,
                FAKE_STORE_PRODUCT_URL+"/{id}",
                createProductRequestDto,
                FakeStoreProductDto.class,
                productId
        );

        return response.getBody();

    }


    public FakeStoreProductDto replaceProduct( CreateProductRequestDto createProductRequestDto,
                                              Long productId ){
        ResponseEntity<FakeStoreProductDto> response = requestForEntity(
                HttpMethod.PUT,
                FAKE_STORE_PRODUCT_URL+"/{id}",
                createProductRequestDto,
                FakeStoreProductDto.class,
                productId
        );

        return response.getBody();
    };


    public String deleteProduct(Long productId) {
        return "Product deleted with ID: "+productId;
    }
}
