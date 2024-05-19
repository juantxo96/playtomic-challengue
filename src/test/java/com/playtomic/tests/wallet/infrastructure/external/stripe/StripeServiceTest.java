package com.playtomic.tests.wallet.infrastructure.external.stripe;


import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
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
        stubFor(post("/v1/stripe-simulator/charges").willReturn(status(422)));

        Assertions.assertThrows(StripeAmountTooSmallException.class, () -> {
            s.charge("4242 4242 4242 4242", new BigDecimal(5));
        });
    }

    @Test
    public void should_return_null_when_500() {
        stubFor(post("/v1/stripe-simulator/charges").willReturn(status(500)));

        var response = s.charge("4242 4242 4242 4242", new BigDecimal(5));
        Assertions.assertNull(response);
    }

    @Test
    public void test_ok() throws StripeServiceException {
        String responseBody = "{\"id\": \"e484e055-56ec-40bb-a271-edb6bf0a4d20\", \"amount\": 15.00}}\"}";
        stubFor(post("/v1/stripe-simulator/charges").willReturn(ok().withHeader("Content-Type", "application/json").withBody(responseBody)));
        Payment payment = s.charge("4242 4242 4242 4242", new BigDecimal(15));

        Assertions.assertEquals(payment.getId(), "e484e055-56ec-40bb-a271-edb6bf0a4d20");
    }
}
