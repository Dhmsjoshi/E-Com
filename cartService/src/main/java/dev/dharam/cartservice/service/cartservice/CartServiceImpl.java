package dev.dharam.cartservice.service.cartservice;

import dev.dharam.cartservice.client.productservice.ProductServiceClient;
import dev.dharam.cartservice.dto.AddToCartRequestDto;
import dev.dharam.cartservice.dto.CartResponseDto;
import dev.dharam.cartservice.exceptions.ResourceNotFoundException;
import dev.dharam.cartservice.mapper.DtoMapper;
import dev.dharam.cartservice.model.Cart;
import dev.dharam.cartservice.model.CartItem;
import dev.dharam.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class CartServiceImpl implements CartService{

    public CartServiceImpl(CartRepository cartRepository,
                           ProductServiceClient productService,
                           DtoMapper dtoMapper,
                           @Qualifier("myCustomRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.cartRepository = cartRepository;
        this.productService = productService;
        this.dtoMapper = dtoMapper;
        this.redisTemplate = redisTemplate;
    }

    private final CartRepository cartRepository;
    private final ProductServiceClient productService;
    private final DtoMapper dtoMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String CART_CACHE_PREFIX = "cart:";


    @Override
    public CartResponseDto addToCart(UUID userId, AddToCartRequestDto requestDto) {
        // 1. External Call: Product details fetch karo (Price & Name ke liye)
        // Agar product invalid hai, toh ye client exception throw karega (404/503)
        var productDetails = productService.getProductDetails(requestDto.productId());

        if (productDetails == null) {
            throw new ResourceNotFoundException("Product not found with ID: " + requestDto.productId());
        }

        // 2. Cart Fetch/Create: User ka cart find karo ya naya banao
        Cart cart = cartRepository.findById(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    newCart.setTotalAmount(0.0);
                    return newCart;
                });

        // 3. Logic: Check karo ki product pehle se cart mein hai ya nahi
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProductid().equals(requestDto.productId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Case A: Product mil gaya, toh sirf Quantity aur latest Price update karo
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + requestDto.quantity());
            item.setPrice(productDetails.price());
            item.setProductName(productDetails.title()); // Security: Sync name too
        } else {
            // Case B: Naya product hai, toh CartItem create karke list mein add karo
            CartItem newItem = CartItem.builder()
                    .productid(productDetails.id())
                    .productName(productDetails.title()) // Entity field updated
                    .price(productDetails.price())
                    .quantity(requestDto.quantity())
                    .build();

            // Link item to cart (Bidirectional relationship handle karega)
            cart.getCartItems().add(newItem);
        }

        // 4. Recalculate: Cart ka total amount update karo
        cart.updateTotalAmount();

        // 5. Save: Database mein save karo (MySQL)
        Cart savedCart = cartRepository.save(cart);

        CartResponseDto response = DtoMapper.mapToCartResponseDto(savedCart);
        saveToCache(userId, response);

        // 6. Response: Entity ko Record DTO mein badal kar return karo
        return response;
    }

    @Override
    public CartResponseDto removeItemFromCart(UUID userId, Long productId) {
        Cart cart= cartRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("Cart not found for User ID: " + userId)
        );

        CartItem itemToRemove = cart.getCartItems()
                .stream()
                .filter(cartItem -> cartItem.getProductid().equals(productId))
                .findFirst()
                .orElseThrow(
                        ()->  new  ResourceNotFoundException("Product with id: "+productId+" not found!")
                );
        if(itemToRemove.getQuantity() > 1){
            itemToRemove.setQuantity(itemToRemove.getQuantity() - 1);
        }else{
            cart.getCartItems().remove(itemToRemove);
        }

        cart.updateTotalAmount();
        Cart updatedCart = cartRepository.save(cart);

        CartResponseDto response = DtoMapper.mapToCartResponseDto(updatedCart);

        //Cache update
        String key = CART_CACHE_PREFIX+userId.toString();
        try{
            //new data overwrite
            redisTemplate.opsForValue().set(key, response, Duration.ofHours(24));
            log.info("Redis cache updated for user: {}"+userId);
        }catch (Exception e){
            log.error("Failed to update redis cache: {}", e.getMessage());
            //delete old data from cache
            redisTemplate.delete(key);
        }

        return response;
    }

    @Override
    public CartResponseDto getCart(UUID userId) {
        String key = CART_CACHE_PREFIX + userId.toString();

        // 1. Try fetching from Redis
        try {
            CartResponseDto cachedCart = (CartResponseDto) redisTemplate.opsForValue().get(key);
            if (cachedCart != null) {
                log.info("Redis Cache hit for User: {}", userId);
                return cachedCart;
            }
        } catch (Exception e) {
            log.error("Redis error: {}", e.getMessage());
        }

        log.info("Redis cache miss. Fetching from DB for user: {}", userId);

        // 2. Fetch from DB
        Cart cart = cartRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("Cart not found for User ID: " + userId)
        );

        CartResponseDto response = DtoMapper.mapToCartResponseDto(cart);

        // 3. FIX: Save to Redis taaki agli request "Hit" ho jaye!
        try {
            saveToCache(userId, response);
        } catch (Exception e) {
            log.error("Failed to populate cache after DB fetch: {}", e.getMessage());
        }

        return response;
    }

    private void saveToCache(UUID userId, CartResponseDto dto){
        String key = CART_CACHE_PREFIX + userId.toString();
        //24 hrs expiry
        redisTemplate.opsForValue().set(key,dto, Duration.ofHours(24));
    }

    //Hybrid strategy
    //For Read -> Cache-Aside strategy
    //For Write-> Write-Update or Write-Through strategy

}
