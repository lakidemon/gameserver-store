package ru.lakidemon.store.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private Long id;
    @OneToOne
    private Order order;
    private String ip;
    private String signature;
    private String gateUrl;
}
