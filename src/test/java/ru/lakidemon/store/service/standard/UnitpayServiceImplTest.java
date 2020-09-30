package ru.lakidemon.store.service.standard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.lakidemon.store.configuration.UnitpayConfiguration;
import ru.lakidemon.store.model.Item;
import ru.lakidemon.store.model.Order;
import ru.lakidemon.store.model.Payment;
import ru.lakidemon.store.repository.PaymentsRepository;
import ru.lakidemon.store.service.OrderService;
import ru.lakidemon.store.unitpay.PaymentStatus;
import ru.lakidemon.store.unitpay.RequestParams;
import ru.lakidemon.store.unitpay.Result;

import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitpayServiceImplTest {
    static final String SECRET_KEY = "12345";
    static final String PUBLIC_KEY = "54321";
    static final String PAYMENT_URL = "https://unitpay.money/pay/";
    static final Currency RUB = Currency.getInstance("RUB");
    @Mock
    private PaymentsRepository paymentsRepository;
    @Mock
    private OrderService orderService;
    private UnitpayServiceImpl unitpayService;

    @BeforeEach
    void setup() {
        var config = new UnitpayConfiguration();
        config.setSecretKey(SECRET_KEY);
        config.setPublicKey(PUBLIC_KEY);
        config.setPaymentUrl(PAYMENT_URL);
        unitpayService = new UnitpayServiceImpl(config, paymentsRepository, orderService);
    }

    @Test
    void shouldGenerateCorrectUrlAndSavePayment() {
        var payment = unitpayService.createPayment(Order.builder()
                .id(1000L)
                .item(Item.builder().name("Test").shortDescription("Description").price(100).build())
                .totalSum(100)
                .placedTime(LocalDateTime.now())
                .customer("Lakidemon")
                .build());
        verify(paymentsRepository).save(payment);
        assertEquals(new StringBuilder(PAYMENT_URL).append(PUBLIC_KEY)
                .append("?sum=100&currency=RUB&account=1000&desc=Description&signature"
                        + "=b7bf9c2b9eea3df8f503c56cd0a2c0845f0eb8a4beb68bdc9ccb5958a1a4cbf4")
                .toString(), payment.getPayLink());
    }

    @Test
    void shouldFailOnUnknownOrder() {
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.empty());
        assertEquals(Result.Message.PAYMENT_NOTFOUND,
                unitpayService.checkPayment(RequestParams.builder().orderId(1).build()).getMessage());
    }

    @Test
    void shouldFailWhenSumDoesntMatch() {
        Order order = Order.builder().totalSum(100).build();
        Payment payment = Payment.builder().order(order).build();
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.of(payment));
        assertEquals(Result.Message.INCORRECT_SUM,
                unitpayService.checkPayment(RequestParams.builder().orderId(1).orderSum(101).build()).getMessage());
    }

    @Test
    void shouldFailWhenCurrencyDoesntMatch() {
        Order order = Order.builder().totalSum(100).build();
        Payment payment = Payment.builder().order(order).build();
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.of(payment));
        assertEquals(Result.Message.INCORRECT_CURRENCY, unitpayService.checkPayment(
                RequestParams.builder().orderId(1).orderSum(100).orderCurrency(Currency.getInstance("USD")).build())
                .getMessage());
    }

    @Test
    void shouldFailWhenOrderIsDispatched() {
        Order order = Order.builder().totalSum(100).build();
        Payment payment = Payment.builder()
                .order(order)
                .currentState(PaymentStatus.CONFIRMED)
                .completeTime(LocalDateTime.now())
                .build();
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.of(payment));
        assertEquals(Result.Message.REPEAT_HANDLING,
                unitpayService.checkPayment(RequestParams.builder().orderId(1).orderSum(100).orderCurrency(RUB).build())
                        .getMessage());
    }

    @Test
    void shouldPassCheckOnCorrectOrder() {
        Order order = Order.builder().totalSum(100).build();
        Payment payment = Payment.builder().order(order).build();
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.of(payment));
        assertEquals(Result.Message.CHECK_OK,
                unitpayService.checkPayment(RequestParams.builder().orderId(1).orderSum(100).orderCurrency(RUB).build())
                        .getMessage());
    }

    @Test
    void shouldFailConfirmWhenOrderWasNotDispatched() {
        Order order = Order.builder().totalSum(100).build();
        Payment payment = Payment.builder().order(order).build();
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.of(payment));
        when(orderService.dispatchOrder(any())).thenReturn(false);

        assertEquals(Result.Message.DISPATCH_FAILED, unitpayService.confirmPayment(
                RequestParams.builder().orderId(1).orderSum(100).orderCurrency(RUB).build()).getMessage());
    }

    @Test
    void shouldMarkPaymentAsFinishedWhenOrderWasDispatched() {
        Order order = Order.builder().totalSum(100).build();
        Payment payment = Payment.builder().order(order).build();
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.of(payment));
        when(orderService.dispatchOrder(any())).thenReturn(true);

        assertEquals(Result.Message.CONFIRM_OK, unitpayService.confirmPayment(
                RequestParams.builder().orderId(1).orderSum(100).orderCurrency(RUB).build()).getMessage());
        assertNotNull(payment.getCompleteTime());
        verify(paymentsRepository).save(payment);
    }

    @Test
    void shouldGenerateCorrectSignature() {
        assertEquals("b7bf9c2b9eea3df8f503c56cd0a2c0845f0eb8a4beb68bdc9ccb5958a1a4cbf4",
                unitpayService.generateSignature(List.of("1000", "RUB", "Description", "100")));
    }
}