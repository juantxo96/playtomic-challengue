package com.playtomic.tests.wallet.api.wallet;

import com.playtomic.tests.wallet.api.response.ErrorResponse;
import com.playtomic.tests.wallet.api.response.Response;
import com.playtomic.tests.wallet.application.wallet.GetWallet;
import com.playtomic.tests.wallet.application.wallet.ProcessPayment;
import com.playtomic.tests.wallet.application.wallet.getWallet.WalletDto;
import com.playtomic.tests.wallet.application.wallet.processPayment.ProcessPaymentRequestDto;
import com.playtomic.tests.wallet.application.wallet.processPayment.ProcessPaymentResponseDto;
import com.playtomic.tests.wallet.domain.transaction.exception.TransactionAmountTooSmallException;
import com.playtomic.tests.wallet.domain.wallet.exception.WalletNotFoundException;
import com.playtomic.tests.wallet.infrastructure.external.stripe.StripeAmountTooSmallException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/wallets")
public class WalletController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletController.class);
    private final GetWallet getWallet;
    private final ProcessPayment processPayment;

    public WalletController(GetWallet getWallet, ProcessPayment processPayment) {
        this.getWallet = getWallet;
        this.processPayment = processPayment;
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<Response> get(@PathVariable UUID walletId) {
        try {
            WalletDto wallet = getWallet.execute(walletId);
            return ResponseEntity.ok(Response.buildSuccess(wallet));
        }
        catch (WalletNotFoundException e) {
            LOGGER.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.internalServerError().body(Response.buildError(ErrorResponse.UNEXPECTED_ERROR));
        }
    }

    @PostMapping("/{walletId}/payment")
    public ResponseEntity<Response> payment(@PathVariable UUID walletId, @RequestBody ProcessPaymentRequestDto paymentRequest) {
        try{
            ProcessPaymentResponseDto processPaymentResponseDto = processPayment.execute(walletId, paymentRequest);
            return ResponseEntity.ok(Response.buildSuccess(processPaymentResponseDto));
        }
        catch (WalletNotFoundException e) {
            LOGGER.warn(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (TransactionAmountTooSmallException | StripeAmountTooSmallException e) {
            LOGGER.warn(e.getMessage());
            return ResponseEntity.badRequest().body(Response.buildError(ErrorResponse.AMOUNT_TOO_LOW));
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage());
            return ResponseEntity.internalServerError().body(Response.buildError(ErrorResponse.UNEXPECTED_ERROR));
        }
    }
}
