package com.hcl.favouritePayee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcl.favouritePayee.dto.CreateFavoriteAccountRequest;
import com.hcl.favouritePayee.dto.FavoritePayeeResponse;
import com.hcl.favouritePayee.dto.UpdateFavoriteAccountRequest;
import com.hcl.favouritePayee.entity.FavoritePayee;
import com.hcl.favouritePayee.service.PayeesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PayeesController.class)
class PayeesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PayeesService payeesService;

    @Autowired
    private ObjectMapper objectMapper;

    private FavoritePayeeResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new FavoritePayeeResponse();
        sampleResponse.setId(1L);
        sampleResponse.setAccountName("John Doe");
        sampleResponse.setIban("NL00BANK0123456789");
    }

    @Test
    void getAllFavoritePayees_ShouldReturnPagedList() throws Exception {
        FavoritePayee entity = new FavoritePayee();
        Page<FavoritePayee> page = new PageImpl<>(Collections.singletonList(entity));

        when(payeesService.getFavoriteAccounts(eq(12345L), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/payee/customer/12345")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void getFavoritePayeeById_ShouldReturnPayee() throws Exception {
        when(payeesService.getFavoriteAccount(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/payee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountName").value("John Doe"))
                .andExpect(jsonPath("$.iban").value("NL00BANK0123456789"));
    }

    @Test
    void createFavoritePayee_ShouldReturnCreatedStatus() throws Exception {
        CreateFavoriteAccountRequest request = new CreateFavoriteAccountRequest();
        request.setAccountName("John Doe");
        request.setIban("NL00BANK0123456789");

        when(payeesService.createFavoriteAccount(eq(12345L), any(CreateFavoriteAccountRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/payee/12345")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateFavoritePayee_ShouldReturnUpdatedPayee() throws Exception {
        UpdateFavoriteAccountRequest request = new UpdateFavoriteAccountRequest();
        request.setAccountName("Jane Doe");

        // Now maps exactly to payeesService.updateFavoriteAccount(id, request)
        when(payeesService.updateFavoriteAccount(eq(1L), any(UpdateFavoriteAccountRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/v1/payee/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFavoritePayees_ShouldReturnNoContent() throws Exception {
        // Now maps exactly to payeesService.deleteFavoriteAccount(id)
        doNothing().when(payeesService).deleteFavoriteAccount(1L);

        mockMvc.perform(delete("/api/v1/payee/1"))
                .andExpect(status().isNoContent());
    }
}