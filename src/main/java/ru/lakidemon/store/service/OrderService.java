package ru.lakidemon.store.service;

import ru.lakidemon.store.model.Item;
import ru.lakidemon.store.model.Order;

public interface OrderService {

    Order createOrder(String customer, Item item);

    boolean dispatchOrder(Order order);

}
