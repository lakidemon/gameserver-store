package ru.lakidemon.store.repository;

import org.springframework.data.repository.CrudRepository;
import ru.lakidemon.store.model.DispatchedOrder;

import java.util.Optional;

public interface DispatchedOrdersRepository extends CrudRepository<DispatchedOrder, Long> {

    Optional<DispatchedOrder> findByItemNameAndPlayer(String itemName, String player);
}
