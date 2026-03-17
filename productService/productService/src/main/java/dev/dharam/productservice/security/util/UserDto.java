package dev.dharam.productservice.security.util;

import java.util.List;
import java.util.UUID;

public record UserDto(UUID userId, String email, List<String> roles) {};
