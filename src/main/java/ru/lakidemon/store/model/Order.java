package ru.lakidemon.store.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private Long id;
    @OneToOne
    private Item item;
    private String customer;
    private int totalSum;
    @Temporal(TemporalType.TIMESTAMP)
    private Date placedTime;

}
