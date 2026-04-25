package com.hcl.favouritePayee.controller;

import com.hcl.favouritePayee.dto.LoginResponse;
import com.hcl.favouritePayee.dto.RegisterRequest;
import com.hcl.favouritePayee.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication and validation APIs")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Validate customer", description = "Validate customer exists in the system using X-Customer-Id header")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer validated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "Customer not found",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content)
    })
    @GetMapping("/validate")
    public ResponseEntity<LoginResponse> validateCustomer(
            @Parameter(description = "Customer ID from header", required = true, example = "1")
            @RequestHeader("X-Customer-Id") Long customerId) {
        LoginResponse response = authService.validateCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Register customer", description = "Register a new customer with provided ID and name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer registered successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "409", description = "Customer with this ID already exists",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> registerCustomer(
            @Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}