package ru.lakidemon.store.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;
    @Column(name = "customer")
    private String customer;
    @Column(name = "total_sum")
    private int totalSum;
    @Column(name = "time_placed")
    private LocalDateTime placedTime;

}
