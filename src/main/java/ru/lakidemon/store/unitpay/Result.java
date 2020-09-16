package ru.lakidemon.store.unitpay;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Value;
import ru.lakidemon.store.converter.ResultSerializer;

@Value
@JsonSerialize(using = ResultSerializer.class)
public class Result {
    private String message;
    private boolean error;

    public static Result error(String message) {
        return new Result(message, true);
    }

    public static Result result(String message) {
        return new Result(message, false);
    }

}
