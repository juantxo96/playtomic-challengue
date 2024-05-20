package com.playtomic.tests.wallet.api.wallet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.playtomic.tests.wallet.application.wallet.processPayment.ProcessPaymentRequestDto;
import com.playtomic.tests.wallet.domain.transaction.Transaction;
import com.playtomic.tests.wallet.domain.transaction.TransactionRepository;
import com.playtomic.tests.wallet.domain.wallet.Wallet;
import com.playtomic.tests.wallet.domain.wallet.WalletRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@Rollback
@WireMockTest(httpPort = 4040)
@ActiveProfiles(profiles = "test")
public class WalletControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Tag("/wallet/{walletId}")
    public void should_return_wallet() throws Exception {
        UUID userId = UUID.randomUUID();
        String currency = "EUR";
        Wallet wallet = new Wallet(userId, currency);

        Wallet persistedWallet = walletRepository.save(wallet);

        mockMvc.perform(MockMvcRequestBuilders.get("/wallets/{id}", persistedWallet.getId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.id").value(wallet.getId().toString()));
    }

    @Test
    @Tag("/wallet/{walletId}")
    public void should_return_not_found_when_no_wallet() throws Exception {
        UUID walletId = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.get("/wallets/{id}", walletId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Tag("/wallet/{walletId}/payment")
    public void should_process_payment() throws Exception {
        UUID userId = UUID.randomUUID();
        String currency = "EUR";
        Wallet wallet = new Wallet(userId, currency);

        Wallet persistedWallet = walletRepository.save(wallet);

        ProcessPaymentRequestDto processPaymentRequestDto = new ProcessPaymentRequestDto("success_card", new BigDecimal("10"));
        String requestBody = objectMapper.writeValueAsString(processPaymentRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/{id}/payment", persistedWallet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.data.wallet_id").value(wallet.getId().toString()))
                .andExpect(jsonPath("$.data.transaction_status").value("COMPLETED"));

        Wallet savedWallet = walletRepository.findById(persistedWallet.getId()).get();
        Assertions.assertEquals(savedWallet.getBalance(), new BigDecimal("10"));
    }

    @Test
    @Tag("/wallet/{walletId}/payment")
    public void should_return_not_found_when_wallet_not_found() throws Exception {
        UUID walletId = UUID.randomUUID();

        ProcessPaymentRequestDto processPaymentRequestDto = new ProcessPaymentRequestDto("success_card", new BigDecimal("10"));
        String requestBody = objectMapper.writeValueAsString(processPaymentRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/{id}/payment", walletId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @Tag("/wallet/{walletId}/payment")
    public void should_return_amount_too_low_error_when_amount_lower_than_10() throws Exception {
        UUID userId = UUID.randomUUID();
        String currency = "EUR";
        Wallet wallet = new Wallet(userId, currency);

        Wallet persistedWallet = walletRepository.save(wallet);

        ProcessPaymentRequestDto processPaymentRequestDto = new ProcessPaymentRequestDto("success_card", new BigDecimal("9"));
        String requestBody = objectMapper.writeValueAsString(processPaymentRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/{id}/payment", persistedWallet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value(2))
                .andExpect(jsonPath("$.error_message").value("Amount too low"));
    }

    @Test
    @Tag("/wallet/{walletId}/payment")
    public void should_return_amount_too_low_error_when_unexpected_error() throws Exception {
        UUID userId = UUID.randomUUID();
        String currency = "EUR";
        Wallet wallet = new Wallet(userId, currency);

        Wallet persistedWallet = walletRepository.save(wallet);

        ProcessPaymentRequestDto processPaymentRequestDto = new ProcessPaymentRequestDto("internal_server_error", new BigDecimal("10"));
        String requestBody = objectMapper.writeValueAsString(processPaymentRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/wallets/{id}/payment", persistedWallet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(jsonPath("$.error_code").value(1))
                .andExpect(jsonPath("$.error_message").value("An unexpected error occurred."));

        Transaction transaction = transactionRepository.findAll().get(0);
        Assertions.assertEquals(transaction.getAmount(), new BigDecimal("10"));
        Assertions.assertEquals(transaction.getStatus().name(), "CANCELLED");
    }
}
