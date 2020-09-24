package ru.lakidemon.store.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "unitpay")
@Data
public class UnitpayConfiguration {
    private String secretKey = "";
    private String publicKey = "";
    private List<String> allowedIPs = Collections.emptyList();
    private String paymentUrl = "https://unitpay.money/pay/";

    void setAllowedIPs(String allowedIPs) {
        this.allowedIPs = Arrays.asList(allowedIPs.split(","));
    }
}
