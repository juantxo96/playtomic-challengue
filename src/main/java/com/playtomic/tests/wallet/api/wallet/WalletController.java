package com.playtomic.tests.wallet.api.wallet;

import com.playtomic.tests.wallet.api.response.ErrorResponse;
import com.playtomic.tests.wallet.api.response.Response;
import com.playtomic.tests.wallet.application.wallet.GetWallet;
import com.playtomic.tests.wallet.application.wallet.WalletDto;
import com.playtomic.tests.wallet.domain.wallet.exception.WalletNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/wallets")
public class WalletController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WalletController.class);
    private final GetWallet getWallet;

    public WalletController(GetWallet getWallet) {
        this.getWallet = getWallet;
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
}
