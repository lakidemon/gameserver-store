package ru.lakidemon.store.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.lakidemon.store.repository.PaymentsRepository;
import ru.lakidemon.store.unitpay.UnitpayService;
import ru.lakidemon.store.unitpay.UnitpayServiceImpl;

import java.util.List;

@Configuration
public class UnitpayConfiguration {

    @Bean
    String secretCode() {
        return "";
    }

    @Bean
    List<String> allowedIPs() {
        return List.of("31.186.100.49", "178.132.203.105", "52.29.152.23", "52.19.56.234");
    }
}
