package ru.lakidemon.store.model;

import lombok.Builder;
import lombok.Data;
import ru.lakidemon.store.unitpay.PaymentStatus;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @Column(name = "pay_link")
    private String payLink;
    @Column(name = "current_state")
    @Builder.Default
    private PaymentStatus currentState = PaymentStatus.PENDING;
    @Column(name = "error_message")
    private String errorMessage;
    @Column(name = "time_finished")
    private LocalDateTime completeTime;
}
