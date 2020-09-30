package ru.lakidemon.store.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.lakidemon.store.model.Order;

@Repository
public interface OrdersRepository extends CrudRepository<Order, Long> {
}
