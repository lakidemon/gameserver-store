package ru.lakidemon.store.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AdminViewController {

    @RequestMapping("/admin")
    String admin() {
        return "admin";
    }

}
