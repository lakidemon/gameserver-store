package ru.lakidemon.store.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "items")
@Data
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String name;
    @Column
    private String shortDesc;
    @Column
    private int price;
}
