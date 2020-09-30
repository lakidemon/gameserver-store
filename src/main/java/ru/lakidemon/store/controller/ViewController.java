package ru.lakidemon.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import ru.lakidemon.store.model.Item;
import ru.lakidemon.store.service.ItemService;

import java.util.Collection;

@Controller
@RequiredArgsConstructor
public class ViewController {
    private final ItemService itemService;

    @GetMapping
    String mainPage() {
        return "index";
    }

    @ModelAttribute("items")
    Collection<Item> items() {
        return itemService.getAllItems();
    }

}
