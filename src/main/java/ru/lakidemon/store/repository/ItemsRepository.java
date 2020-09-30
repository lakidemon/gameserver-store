package ru.lakidemon.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.lakidemon.store.model.Item;

import java.util.Optional;

@Repository
public interface ItemsRepository extends JpaRepository<Item, Long> {

    Optional<Item> findByName(String itemName);

}
