package ru.lakidemon.store.dto;

import lombok.Builder;

@Builder
public class OrderResult {
    private String paymentLink;
    private String message;
}
