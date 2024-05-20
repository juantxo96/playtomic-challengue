package com.playtomic.tests.wallet.infrastructure.external.stripe;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.playtomic.tests.wallet.domain.payment.Payment;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.math.BigDecimal;
import java.net.URI;

/**
 * This test is failing with the current implementation.
 *
 * How would you test this?
 */
@WireMockTest(httpPort = 4040)
public class StripeServiceTest {
    URI testUri = URI.create("http://localhost:4040/v1/stripe-simulator/charges");
    StripeService s = new StripeService(testUri, testUri, new RestTemplateBuilder());

    @Test
    public void should_return_amount_too_small_exception_when_422() {
        Assertions.assertThrows(StripeAmountTooSmallException.class, () -> {
            s.charge("success_card", new BigDecimal(9));
        });
    }

    @Test
    public void should_return_null_when_500() {
        Assertions.assertThrows(StripeServiceException.class, () -> {
            s.charge("internal_server_error", new BigDecimal(10));
        });
    }

    @Test
    public void test_ok() throws StripeServiceException {
        Payment payment = s.charge("success_card", new BigDecimal(10));

        Assertions.assertEquals(payment.getId(), "e484e055-56ec-40bb-a271-edb6bf0a4d20");
    }
}
