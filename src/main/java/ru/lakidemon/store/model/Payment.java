package ru.lakidemon.store.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @Column(name = "ip")
    private String ip;
    @Column(name = "signature")
    private String signature;
    @Column(name = "pay_link")
    private String payLink;
    @Column(name = "time_finished")
    private LocalDateTime completeTime;
}
