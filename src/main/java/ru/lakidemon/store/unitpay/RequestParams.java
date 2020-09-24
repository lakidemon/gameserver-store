package ru.lakidemon.store.unitpay;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Currency;

@Data
@Builder
public class RequestParams {
    private int unitpayId;
    private int projectId;
    @JsonProperty("account")
    private long orderId;
    private double payerSum;
    private Currency payerCurrency;
    private double profit;
    private String phone;
    private String paymentType;
    private double orderSum;
    private Currency orderCurrency;
    private String operator;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private String errorMessage;
    private String signature;

}
