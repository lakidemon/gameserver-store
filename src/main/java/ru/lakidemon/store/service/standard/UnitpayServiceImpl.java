package ru.lakidemon.store.service.standard;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import ru.lakidemon.store.configuration.UnitpayConfiguration;
import ru.lakidemon.store.model.Order;
import ru.lakidemon.store.model.Payment;
import ru.lakidemon.store.repository.PaymentsRepository;
import ru.lakidemon.store.service.OrderService;
import ru.lakidemon.store.service.UnitpayService;
import ru.lakidemon.store.unitpay.PaymentStatus;
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
    private final UnitpayConfiguration unitpayConfig;
    private final PaymentsRepository paymentsRepository;
    private final OrderService orderService;

    @Override
    public Payment createPayment(Order order) {
        var sign = generateSignature(
                Stream.of(order.getId(), unitpayConfig.getCurrency().getCurrencyCode(), order.getItem().getShortDesc(),
                        order.getTotalSum()).map(Objects::toString).collect(Collectors.toList()));
        var url = UriComponentsBuilder.fromHttpUrl(unitpayConfig.getPaymentUrl() + unitpayConfig.getPublicKey())
                .queryParam("sum", order.getTotalSum())
                .queryParam("currency", unitpayConfig.getCurrency().getCurrencyCode())
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
        // TODO: log
        var paymentOpt = paymentsRepository.findByOrderId(params.getOrderId());
        if (paymentOpt.isEmpty()) {
            return Result.error(Result.Message.PAYMENT_NOTFOUND);
        }
        var payment = paymentOpt.get();
        if (payment.getOrder().getTotalSum() != (int) params.getOrderSum()) {
            return Result.error(Result.Message.INCORRECT_SUM);
        }
        if (!unitpayConfig.getCurrency().equals(params.getOrderCurrency())) {
            return Result.error(Result.Message.INCORRECT_CURRENCY);
        }
        if (payment.getCompleteTime() != null) {
            return Result.error(Result.Message.REPEAT_HANDLING);
        }
        return Result.result(Result.Message.CHECK_OK);
    }

    @Override
    public Result confirmPayment(RequestParams params) {
        // TODO: log
        var recheck = checkPayment(params);
        if (recheck.isError()) {
            return recheck;
        }
        return paymentsRepository.findByOrderId(params.getOrderId()).map(payment -> {
            if (!orderService.dispatchOrder(payment.getOrder())) {
                return Result.error(Result.Message.DISPATCH_FAILED);
            }
            payment.setCompleteTime(LocalDateTime.now());
            payment.setCurrentState(PaymentStatus.CONFIRMED);
            paymentsRepository.save(payment);
            return Result.result(Result.Message.CONFIRM_OK);
        }).orElse(Result.error(Result.Message.PAYMENT_NOTFOUND));
    }

    @Override
    public Result handleError(RequestParams params) {
        var paymentOpt = paymentsRepository.findByOrderId(params.getOrderId());
        if (paymentOpt.isEmpty()) {
            return Result.error(Result.Message.PAYMENT_NOTFOUND);
        }
        var payment = paymentOpt.get();
        // TODO: log
        payment.setCurrentState(PaymentStatus.ERROR);
        payment.setErrorMessage(params.getErrorMessage());
        paymentsRepository.save(payment);
        return Result.result(Result.Message.OK); // not terminal operation
    }

    @Override
    public boolean validateSignature(String reference, List<String> orderedValues) {
        return Objects.equals(reference, generateSignature(orderedValues));
    }

    @Override
    public String generateSignature(List<String> orderedValues) {
        var input = String.join(SIGN_DELIMITER, orderedValues)
                .concat(SIGN_DELIMITER)
                .concat(unitpayConfig.getSecretKey());
        return Hashing.sha256().hashString(input, StandardCharsets.UTF_8).toString();
    }
}
