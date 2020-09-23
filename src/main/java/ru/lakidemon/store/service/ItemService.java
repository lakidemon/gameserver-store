package ru.lakidemon.store.service;

import ru.lakidemon.store.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemService {

    Optional<Item> getItem(String name);

    Collection<Item> getAllItems();

    void saveItem(Item item);

    boolean canBuy(String customer, Item what);

}
