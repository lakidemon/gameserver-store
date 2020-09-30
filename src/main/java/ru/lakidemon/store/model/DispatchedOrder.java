package ru.lakidemon.store.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispatched_orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class DispatchedOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "item_name")
    private String itemName;
    @Column(name = "player_name")
    private String player;
    @Column(name = "time_bought")
    @Builder.Default
    private LocalDateTime boughtTime = LocalDateTime.now();
    @Column(name = "received")
    @Builder.Default
    private boolean received = false;
}