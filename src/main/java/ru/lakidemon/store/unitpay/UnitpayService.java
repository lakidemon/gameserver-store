package ru.lakidemon.store.unitpay;

import ru.lakidemon.store.model.Order;
import ru.lakidemon.store.model.Payment;

import java.util.List;

public interface UnitpayService {

    Payment createPayment(Order order);

    Result checkPayment(RequestParams params);

    Result handleError(RequestParams params);

    Result confirmPayment(RequestParams params);

    boolean validateSignature(String reference, List<String> orderedValues);

    String generateSignature(List<String> orderedValues);

}
