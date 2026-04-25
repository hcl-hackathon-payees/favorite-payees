package com.hcl.favouritePayee.controller;

import com.hcl.favouritePayee.dto.CreateFavoriteAccountRequest;
import com.hcl.favouritePayee.dto.FavoritePayeeResponse;
import com.hcl.favouritePayee.dto.UpdateFavoriteAccountRequest;
import com.hcl.favouritePayee.entity.FavoritePayee;
import com.hcl.favouritePayee.service.PayeesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Payees", description = "Favorite payees management APIs")
public class PayeesController {

    @Autowired
    private final PayeesService payeesService;

    //get all payees
    @Operation(summary = "Get All favorite payees", description = "Retrieve paginated list of favorite payees for a customer, ordered by newest first")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved favorite payees",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FavoritePayeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    @GetMapping("/payee/customer/{customerId}")
    public ResponseEntity<Page<FavoritePayee>> getAllFavoritePayees(
            @Parameter(description = "Customer ID", required = true, example = "12345")
            @PathVariable Long customerId,
            @Parameter(description = "Page number", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "5")
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FavoritePayee> accounts = payeesService.getFavoriteAccounts(customerId, pageable);
        return ResponseEntity.ok(accounts);
    }

    //get payees by id
    @Operation(summary = "Get favorite payee by ID", description = "Retrieve a single favorite payee by its ID for a specific customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved favorite payee",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FavoritePayeeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Favorite payee not found",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    @GetMapping("/payee/{id}")
    public ResponseEntity<FavoritePayeeResponse> getFavoritePayeeById(
            @PathVariable Long id) {
        FavoritePayeeResponse account = payeesService.getFavoriteAccount(id);
        return ResponseEntity.ok(account);
    }

    //add payee
    @Operation(summary = "Create favorite payee", description = "Create a new favorite payee. Bank is resolved from IBAN automatically.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Favorite payee created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FavoritePayeeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    @PostMapping("/payee/{customerId}")
    public ResponseEntity<FavoritePayeeResponse> createFavoritePayee(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateFavoriteAccountRequest request) {
        FavoritePayeeResponse account = payeesService.createFavoriteAccount(customerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }


    //update payee
    @Operation(summary = "Update favorite payee", description = "Update an existing favorite payee. Bank is re-resolved from IBAN if changed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorite payee updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = FavoritePayeeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Favorite payee not found",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    @PutMapping("/payee/{id}")
    public ResponseEntity<FavoritePayeeResponse> updateFavoritePayee(
            @PathVariable Long id,
            @RequestBody UpdateFavoriteAccountRequest request) {
        FavoritePayeeResponse account = payeesService.updateFavoriteAccount(id, request);
        return ResponseEntity.ok(account);
    }


    //delete payees
    @Operation(summary = "Delete favorite payee", description = "Delete an existing favorite payee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Favorite payee deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Favorite payee not found",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content)
    })
    @DeleteMapping("/payee/{id}")
    public ResponseEntity<Void> deleteFavoritePayees(
            @PathVariable Long id) {
        payeesService.deleteFavoriteAccount(id);
        return ResponseEntity.noContent().build();
    }
}