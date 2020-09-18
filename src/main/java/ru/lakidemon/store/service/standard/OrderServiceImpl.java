package ru.lakidemon.store.service.standard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.lakidemon.store.model.Item;
import ru.lakidemon.store.model.Order;
import ru.lakidemon.store.service.OrderService;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    @Override
    public Order createOrder(String customer, Item item) {
        return null;
    }

    @Override
    public boolean dispatchOrder(Order order) {
        return true;
    }
}
