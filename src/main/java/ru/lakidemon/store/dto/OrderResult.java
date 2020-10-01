package ru.lakidemon.store.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResult {
    private String paymentLink;
    private String message;
}
