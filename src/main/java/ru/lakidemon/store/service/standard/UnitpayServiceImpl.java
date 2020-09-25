package ru.lakidemon.store.service.standard;

import com.google.common.hash.Hashing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UnitpayServiceImpl implements UnitpayService {
    private static final String CHECK = "CHECK:";
    private static final String ERROR = "ERROR:";
    private static final String CONFIRM = "CONFIRM:";

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
        var payment = getPaymentOrPrintError(params, CHECK);
        if (payment == null) {
            return Result.error(Result.Message.PAYMENT_NOTFOUND);
        }
        var result = checkRequestDetails(CHECK, params, payment);
        if (!result.isError()) {
            log.info("{} OK. ID: {}, UnitPay ID: {}", CHECK, params.getOrderId(), params.getUnitpayId());
        }
        return result;
    }

    @Override
    public Result confirmPayment(RequestParams params) {
        var payment = getPaymentOrPrintError(params, CONFIRM);
        if (payment == null) {
            return Result.error(Result.Message.PAYMENT_NOTFOUND);
        }
        var recheck = checkRequestDetails(CONFIRM, params, payment);
        if (recheck.isError()) {
            return recheck;
        }
        if (!orderService.dispatchOrder(payment.getOrder())) {
            log.warn("{} Failed to dispatch order. ID: {}, UnitPay ID: {}", CONFIRM, params.getOrderId(),
                    params.getUnitpayId());
            return Result.error(Result.Message.DISPATCH_FAILED);
        }
        payment.setCompleteTime(LocalDateTime.now());
        payment.setCurrentState(PaymentStatus.CONFIRMED);
        paymentsRepository.save(payment);
        log.info("{} Dispatched order for {}. ID: {}, UnitPay ID: {}", CONFIRM, payment.getOrder().getCustomer(),
                params.getOrderId(), params.getUnitpayId());
        return Result.result(Result.Message.CONFIRM_OK);
    }

    @Override
    public Result handleError(RequestParams params) {
        var payment = getPaymentOrPrintError(params, ERROR);
        if (payment == null) {
            return Result.error(Result.Message.PAYMENT_NOTFOUND);
        }
        payment.setCurrentState(PaymentStatus.ERROR);
        payment.setErrorMessage(params.getErrorMessage());
        paymentsRepository.save(payment);
        log.warn("{} UnitPay reported error: {}. ID: {}, UnitPay ID: {}", ERROR, params.getErrorMessage(),
                params.getOrderId(), params.getUnitpayId());
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

    private Payment getPaymentOrPrintError(RequestParams params, String phase) {
        var paymentOpt = paymentsRepository.findByOrderId(params.getOrderId());
        if (paymentOpt.isEmpty()) {
            log.warn("{} Unknown order {}. UnitPay ID: {}", phase, params.getOrderId(), params.getUnitpayId());
            return null;
        }
        return paymentOpt.get();
    }

    private Result checkRequestDetails(String phase, RequestParams params, Payment payment) {
        if (payment.getCurrentState() == PaymentStatus.CONFIRMED) {
            log.warn("{} Trying to handle already confirmed order. ID: {}, UnitPay ID: {}", phase,
                    params.getOrderCurrency(), unitpayConfig.getCurrency(), params.getOrderId(), params.getUnitpayId());
            return Result.error(Result.Message.REPEAT_HANDLING);
        }
        if (payment.getOrder().getTotalSum() != (int) params.getOrderSum()) {
            log.warn("{} Incorrect sum {} ({} expected). ID: {}, UnitPay ID: {}", CHECK, params.getOrderSum(),
                    payment.getOrder().getTotalSum(), params.getOrderId(), params.getUnitpayId());
            return Result.error(Result.Message.INCORRECT_SUM);
        }
        if (!unitpayConfig.getCurrency().equals(params.getOrderCurrency())) {
            log.warn("{} Incorrect currency {} ({} expected). ID: {}, UnitPay ID: {}", CHECK, params.getOrderCurrency(),
                    unitpayConfig.getCurrency(), params.getOrderId(), params.getUnitpayId());
            return Result.error(Result.Message.INCORRECT_CURRENCY);
        }
        return Result.result(Result.Message.CHECK_OK);
    }
}
