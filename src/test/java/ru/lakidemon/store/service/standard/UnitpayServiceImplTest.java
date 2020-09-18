package ru.lakidemon.store.service.standard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import ru.lakidemon.store.model.Item;
import ru.lakidemon.store.model.Order;
import ru.lakidemon.store.model.Payment;
import ru.lakidemon.store.repository.PaymentsRepository;
import ru.lakidemon.store.service.OrderService;
import ru.lakidemon.store.unitpay.RequestParams;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UnitpayServiceImplTest {
    static final String SECRET_KEY = "12345";
    static final String PUBLIC_KEY = "54321";
    @Mock
    private PaymentsRepository paymentsRepository;
    @Mock
    private OrderService orderService;
    private UnitpayServiceImpl unitpayService;

    @BeforeEach
    void setup() {
        unitpayService = new UnitpayServiceImpl(SECRET_KEY, PUBLIC_KEY, paymentsRepository, orderService);
    }

    @Test
    void shouldGenerateCorrectUrlAndSavePayment() {
        var payment = unitpayService.createPayment(Order.builder()
                .id(1000L)
                .item(Item.builder().name("Test").shortDesc("Description").price(100).build())
                .totalSum(100)
                .placedTime(LocalDateTime.now())
                .customer("Lakidemon")
                .build());
        verify(paymentsRepository).save(payment);
        assertEquals(new StringBuilder("https://unitpay.money/pay/").append(PUBLIC_KEY)
                .append("?sum=100&account=1000&desc=Description&signature"
                        + "=7448269e1c169fed23716d4f3a6d8d4af6c83075c610e2f720a21dd41f80f9c8")
                .toString(), payment.getPayLink());
    }

    @Test
    void shouldFailOnUnknownOrder() {
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.empty());
        assertEquals("Платёж 1 не найден",
                unitpayService.checkPayment(RequestParams.builder().orderId(1).build()).getMessage());
    }

    @Test
    void shouldFailWhenSumDoesntMatch() {
        Order order = Order.builder().totalSum(100).build();
        Payment payment = Payment.builder().order(order).build();
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.of(payment));
        assertEquals("Некорректная сумма платежа: 100 != 101",
                unitpayService.checkPayment(RequestParams.builder().orderId(1).orderSum(101).build()).getMessage());
    }

    @Test
    void shouldFailWhenOrderIsDispatched() {
        Order order = Order.builder().totalSum(100).build();
        Payment payment = Payment.builder().order(order).completeTime(LocalDateTime.now()).build();
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.of(payment));
        assertEquals("Повторная обработка платежа",
                unitpayService.checkPayment(RequestParams.builder().orderId(1).orderSum(100).build()).getMessage());
    }

    @Test
    void shouldPassCheckOnCorrectOrder() {
        Order order = Order.builder().totalSum(100).build();
        Payment payment = Payment.builder().order(order).build();
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.of(payment));
        assertEquals("Всё отлично!",
                unitpayService.checkPayment(RequestParams.builder().orderId(1).orderSum(100).build()).getMessage());
    }

    @Test
    void shouldFailConfirmWhenOrderWasNotDispatched() {
        Order order = Order.builder().totalSum(100).build();
        Payment payment = Payment.builder().order(order).build();
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.of(payment));
        when(orderService.dispatchOrder(any())).thenReturn(false);

        assertEquals("Не удалось произвести выдачу товара",
                unitpayService.confirmPayment(RequestParams.builder().orderId(1).orderSum(100).build()).getMessage());
    }

    @Test
    void shouldMarkPaymentAsFinishedWhenOrderWasDispatched() {
        Order order = Order.builder().totalSum(100).build();
        Payment payment = Payment.builder().order(order).build();
        when(paymentsRepository.findByOrderId(1)).thenReturn(Optional.of(payment));
        when(orderService.dispatchOrder(any())).thenReturn(true);

        assertEquals("ОК",
                unitpayService.confirmPayment(RequestParams.builder().orderId(1).orderSum(100).build()).getMessage());
        assertNotNull(payment.getCompleteTime());
        verify(paymentsRepository).save(payment);
    }

    @Test
    void shouldGenerateCorrectSignature() {
        assertEquals("7448269e1c169fed23716d4f3a6d8d4af6c83075c610e2f720a21dd41f80f9c8",
                unitpayService.generateSignature(List.of("1000", "Description", "100")));
    }
}