package ru.lakidemon.store.unitpay;

import lombok.Getter;

@Getter
public enum RequestMethod {
    CHECK, PAY, ERROR, PREAUTH;
}
