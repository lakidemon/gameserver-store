package ru.lakidemon.store.model;

import lombok.*;

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
    @Column(name = "time_finished")
    private LocalDateTime completeTime;
}
