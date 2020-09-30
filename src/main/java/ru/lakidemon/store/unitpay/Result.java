package ru.lakidemon.store.unitpay;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.lakidemon.store.converter.ResultSerializer;

@Value
@JsonSerialize(using = ResultSerializer.class)
public class Result {
    private Message message;
    private boolean error;

    public static Result error(Message message) {
        return new Result(message, true);
    }

    public static Result result(Message message) {
        return new Result(message, false);
    }

    @RequiredArgsConstructor
    public enum Message {
        OK("OK"),
        CHECK_OK("Всё отлично!"),
        CONFIRM_OK("Платёж обработан"),
        PAYMENT_NOTFOUND("Платёж не найден"),
        INCORRECT_SUM("Некорректная сумма платежа"),
        INCORRECT_CURRENCY("Некорректная валюта платежа"),
        REPEAT_HANDLING("Попытка обработки обработанного платежа"),
        DISPATCH_FAILED("Не удалось произвести выдачу товара"),
        SIGNATURE_MISMATCH("Подпись не совпала"),
        UNEXPECTED_METHOD("Неизвестный метод");

        @JsonValue
        private final String text;

    }
}
