package dev.dharam.orderservice.exception;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(name = "ErrorResponse", description = "Standard structure for all API error responses")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponseDto(
        @Schema(description = "HTTP Status Code", example = "404")
        int status,

        @Schema(description = "Error message explaining what went wrong", example = "Product not found")
        String message,

        @Schema(description = "Epoch timestamp of the error", example = "1709823456000")
        long timestamp,

        @Schema(description = "Field-wise validation errors (if any)",
                example = "{ \"price\": \"must be greater than 0\", \"title\": \"cannot be empty\" }")
        Map<String, String> details
){

    public ErrorResponseDto(int status, String message) {
        this(status, message, System.currentTimeMillis(),null);
    }
};
