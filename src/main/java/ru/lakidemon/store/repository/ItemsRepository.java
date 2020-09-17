package ru.lakidemon.store.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.lakidemon.store.model.Item;

@Repository
public interface ItemsRepository extends CrudRepository<Item, Long> {
}
