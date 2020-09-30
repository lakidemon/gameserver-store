package ru.lakidemon.store.service;

import ru.lakidemon.store.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    Optional<Item> getItem(String name);

    List<Item> getAllItems();

    void saveItem(Item item);

    boolean canBuy(String customer, Item what);

}
