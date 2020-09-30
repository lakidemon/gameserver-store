package ru.lakidemon.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.lakidemon.store.model.Order;

@Repository
public interface OrdersRepository extends JpaRepository<Order, Long> {
}
