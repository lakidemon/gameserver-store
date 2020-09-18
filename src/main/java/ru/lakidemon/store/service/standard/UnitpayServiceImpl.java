package ru.lakidemon.store.service.standard;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.lakidemon.store.model.Order;
import ru.lakidemon.store.model.Payment;
import ru.lakidemon.store.repository.PaymentsRepository;
import ru.lakidemon.store.service.OrderService;
import ru.lakidemon.store.service.UnitpayService;
import ru.lakidemon.store.unitpay.RequestParams;
import ru.lakidemon.store.unitpay.Result;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class UnitpayServiceImpl implements UnitpayService {
    private static final String SIGN_DELIMITER = "{up}";
    private static final String UNITPAY = "https://unitpay.money/pay/";
    @Qualifier("secretKey")
    private final String secretKey;
    @Qualifier("publicKey")
    private final String publicKey;
    private final PaymentsRepository paymentsRepository;
    private final OrderService orderService;

    @Override
    public Payment createPayment(Order order) {
        var sign = generateSignature(Stream.of(order.getId(), order.getItem().getShortDesc(), order.getTotalSum())
                .map(Objects::toString)
                .collect(Collectors.toList()));
        var url = UriComponentsBuilder.fromHttpUrl(UNITPAY + publicKey)
                .queryParam("sum", order.getTotalSum())
                .queryParam("account", order.getId())
                .queryParam("desc", order.getItem().getShortDesc())
                .queryParam("signature", sign)
                .toUriString();
        var payment = Payment.builder().payLink(url).order(order).build();
        paymentsRepository.save(payment);
        return payment;
    }

    @Override
    public Result checkPayment(RequestParams params) {
        var paymentOpt = paymentsRepository.findByOrderId(params.getOrderId());
        if (paymentOpt.isEmpty()) {
            return Result.error(String.format("Платёж %d не найден", params.getOrderId()));
        }
        var payment = paymentOpt.get();
        if (payment.getOrder().getTotalSum() != (int) params.getOrderSum()) {
            return Result.error(String.format("Некорректная сумма платежа: %d != %d", payment.getOrder().getTotalSum(),
                    (int) params.getOrderSum()));
        }
        if (payment.getCompleteTime() != null) {
            return Result.error("Повторная обработка платежа");
        }
        return Result.result("Всё отлично!");
    }

    @Override
    public Result confirmPayment(RequestParams params) {
        var recheck = checkPayment(params);
        if (recheck.isError()) {
            return recheck;
        }
        var payment = paymentsRepository.findByOrderId(params.getOrderId()).get();
        if (!orderService.dispatchOrder(payment.getOrder())) {
            return Result.error("Не удалось произвести выдачу товара");
        }
        payment.setCompleteTime(LocalDateTime.now());
        paymentsRepository.save(payment);
        return Result.result("ОК");
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
        var input = String.join(SIGN_DELIMITER, orderedValues).concat(SIGN_DELIMITER).concat(secretKey);
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }
}
