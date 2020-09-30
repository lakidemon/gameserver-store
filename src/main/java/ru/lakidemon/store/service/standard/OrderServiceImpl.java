package ru.lakidemon.store.service.standard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.lakidemon.store.model.DispatchedOrder;
import ru.lakidemon.store.model.Item;
import ru.lakidemon.store.model.Order;
import ru.lakidemon.store.repository.DispatchedOrdersRepository;
import ru.lakidemon.store.repository.OrdersRepository;
import ru.lakidemon.store.service.OrderService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrdersRepository ordersRepository;
    private final DispatchedOrdersRepository dispatchedRepository;

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
    public boolean dispatchOrder(Order order) {
        // this is demo
        dispatchedRepository.save(
                DispatchedOrder.builder().itemName(order.getItem().getName()).player(order.getCustomer()).build());
        return true;
    }

}
