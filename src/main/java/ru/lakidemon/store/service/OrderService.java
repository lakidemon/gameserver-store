package ru.lakidemon.store.service;

import ru.lakidemon.store.model.Item;
import ru.lakidemon.store.model.Order;

public interface OrderService {

    Order createOrder(String customer, Item item);

    boolean canOrder(String customer, Item what);

    boolean dispatchOrder(Order order);

}
