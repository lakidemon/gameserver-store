package ru.lakidemon.store.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.lakidemon.store.model.Item;
import ru.lakidemon.store.model.Payment;

import java.util.Optional;

@Repository
public interface ItemsRepository extends CrudRepository<Item, Long> {

    Optional<Item> findByName(String itemName);

}
