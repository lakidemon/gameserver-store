package ru.lakidemon.store.unitpay;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.lakidemon.store.model.Order;
import ru.lakidemon.store.model.Payment;
import ru.lakidemon.store.repository.PaymentsRepository;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UnitpayServiceImpl implements UnitpayService {
    private static final String SIGN_DELIMITER = "{up}";
    @Qualifier("secretCode")
    private final String secret;
    private final PaymentsRepository paymentsRepository;

    @Override
    public Payment createPayment(Order order) {
        return null;
    }

    @Override
    public Result checkPayment(RequestParams params) {

        return null;
    }

    @Override
    public Result confirmPayment(RequestParams params) {
        return null;
    }

    @Override
    public Result handleError(RequestParams params) {
        return Result.result("OK"); // not terminal operation
    }

    @Override
    public boolean validateSignature(String reference, List<String> orderedValues) {
        return Objects.equals(reference, generateSignature(orderedValues));
    }

    @Override
    public String generateSignature(List<String> orderedValues) {
        var input = String.join(SIGN_DELIMITER, orderedValues).concat(SIGN_DELIMITER).concat(secret);
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }
}
