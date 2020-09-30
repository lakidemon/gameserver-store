package ru.lakidemon.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.lakidemon.store.dto.OrderResult;
import ru.lakidemon.store.service.ItemService;
import ru.lakidemon.store.service.OrderService;
import ru.lakidemon.store.service.UnitpayService;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UnitpayService unitpayService;
    private final ItemService itemService;

    @GetMapping("/buyItem")
    @ResponseBody
    public OrderResult makeOrder(@RequestParam("player") String playerName, @RequestParam("item") String itemName) {
        var itemOpt = itemService.getItem(itemName);
        if (itemOpt.isEmpty()) {
            return OrderResult.builder().message("Товар не найден").build();
        }
        var item = itemOpt.get();
        if (!itemService.canBuy(playerName, item)) {
            return OrderResult.builder().message("Вы не можете купить этот товар").build();
        }

        var order = orderService.createOrder(playerName, item);
        var payment = unitpayService.createPayment(order);

        return OrderResult.builder().paymentLink(payment.getPayLink()).build();
    }

}
