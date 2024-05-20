package com.playtomic.tests.wallet.domain.payment;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentServiceImp implements PaymentService {
    private final PaymentRepository paymentRepository;

    public PaymentServiceImp(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment processPayment(String creditCard, BigDecimal amount) {
        return paymentRepository.charge(creditCard, amount);
    }
}
