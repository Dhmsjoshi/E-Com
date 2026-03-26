package dev.dharam.authservice.oAuth2.controllers;

import dev.dharam.authservice.oAuth2.dtos.ClientRegistrationDto;
import dev.dharam.authservice.oAuth2.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Client Management", description = "Endpoints for registering and managing OAuth2 Clients")
public class ClientController {

    private final ClientService clientService;

    @Operation(
            summary = "Register a new OAuth2 Client",
            description = "Creates a new client in the database with Authorization Code and Refresh Token grants."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Client ID already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody ClientRegistrationDto dto) {
        clientService.registerClient(dto);
        return new ResponseEntity<>("Client registered successfully!", HttpStatus.CREATED);
    }
}