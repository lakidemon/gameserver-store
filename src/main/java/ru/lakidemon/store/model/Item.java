package ru.lakidemon.store.model;

import lombok.*;

import javax.persistence.*;
import java.util.Currency;

@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
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
    @Column(name = "discount")
    private double discount;

    public int getPriceWithDiscount() {
        return (int) (price - (price * discount)); // remainder is not needed
    }

}
