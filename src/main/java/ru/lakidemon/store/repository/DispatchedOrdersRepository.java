package ru.lakidemon.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lakidemon.store.model.DispatchedOrder;

import java.util.Optional;

public interface DispatchedOrdersRepository extends JpaRepository<DispatchedOrder, Long> {

    Optional<DispatchedOrder> findByItemNameAndPlayer(String itemName, String player);
}
