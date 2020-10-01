package ru.lakidemon.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.*;
import ru.lakidemon.store.dto.OrderResult;
import ru.lakidemon.store.service.ItemService;
import ru.lakidemon.store.service.OrderService;
import ru.lakidemon.store.service.UnitpayService;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final UnitpayService unitpayService;
    private final ItemService itemService;
    private final MessageSource messageSource;

    @RequestMapping("/buyItem")
    @ResponseBody
    public OrderResult makeOrder(Locale locale, @RequestParam("player") String playerName,
            @RequestParam("item") String itemName) {
        var itemOpt = itemService.getItem(itemName);
        if (itemOpt.isEmpty()) {
            var message = messageSource.getMessage("order.itemNotFound", null, locale);
            return OrderResult.builder().message(message).build();
        }
        var item = itemOpt.get();
        if (!itemService.canBuy(playerName, item)) {
            return OrderResult.builder().message(messageSource.getMessage("order.cantOrder", null, locale)).build();
        }

        var order = orderService.createOrder(playerName, item);
        var payment = unitpayService.createPayment(order);

        return OrderResult.builder().paymentLink(payment.getPayLink()).build();
    }

}
