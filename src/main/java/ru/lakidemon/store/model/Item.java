package ru.lakidemon.store.model;

import lombok.*;

import javax.persistence.*;
import java.util.Currency;

@Entity
@Table(name = "items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "item_name", unique = true)
    private String name;
    @Column(name = "display_name")
    private String displayName;
    @Column(name = "short_description")
    private String shortDescription;
    @Column(name = "full_description")
    private String fullDescription;
    @Column(name = "price")
    private int price;
    @Column(name = "discount")
    private double discount;

    public int getPriceWithDiscount() {
        return (int) (price - (price * discount)); // remainder is not needed
    }

}
