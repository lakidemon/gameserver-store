package ru.lakidemon.store.service.standard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.lakidemon.store.model.Item;
import ru.lakidemon.store.model.Order;
import ru.lakidemon.store.repository.OrdersRepository;
import ru.lakidemon.store.service.OrderService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrdersRepository ordersRepository;

    @Override
    public Order createOrder(String customer, Item item) {
        var order = Order.builder()
                .totalSum(item.getPriceWithDiscount())
                .customer(customer)
                .placedTime(LocalDateTime.now())
                .item(item)
                .build();
        ordersRepository.save(order);
        return order;
    }

    @Override
    public boolean canOrder(String customer, Item what) {
        // some checks on game server here(eg. false if player already has this item)
        return true;
    }

    @Override
    public boolean dispatchOrder(Order order) {
        // ask game server to dispatch order
        return true;
    }

}
