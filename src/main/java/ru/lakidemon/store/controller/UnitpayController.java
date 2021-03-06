package ru.lakidemon.store.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.lakidemon.store.unitpay.RequestMethod;
import ru.lakidemon.store.unitpay.RequestParams;
import ru.lakidemon.store.unitpay.Result;
import ru.lakidemon.store.service.UnitpayService;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UnitpayController {
    private final UnitpayService unitpayService;
    private final ObjectMapper objectMapper;

    @GetMapping("/unitpay")
    @ResponseBody
    public Result handle(@RequestParam("method") RequestMethod method, @RequestParam Map<String, String> allParams) {
        var paramsMap = extractParams(allParams);
        var valuesList = paramsMap.keySet()
                .stream()
                .filter(k -> !k.toLowerCase().startsWith("sign"))
                .map(paramsMap::get)
                .collect(Collectors.toCollection(LinkedList::new));
        valuesList.addFirst(method.name().toLowerCase());
        var requestParams = objectMapper.convertValue(paramsMap, RequestParams.class);
        log.info("{} UnitPay request. PARAMS: {}", method, requestParams);
        if (!unitpayService.validateSignature(requestParams.getSignature(), valuesList)) {
            log.error("Signature mismatch. Order ID: {}, UnitPay ID: {}, Sign: {}, Values: {}",
                    requestParams.getOrderId(), requestParams.getUnitpayId(), requestParams.getSignature(), valuesList);
            return Result.error(Result.Message.SIGNATURE_MISMATCH);
        }
        return handleRequest(method, requestParams);
    }

    public Result handleRequest(RequestMethod method, RequestParams requestParams) {
        switch (method) {
        case PAY:
            return unitpayService.confirmPayment(requestParams);
        case CHECK:
            return unitpayService.checkPayment(requestParams);
        case ERROR:
            return unitpayService.handleError(requestParams);
        case PREAUTH:
            return Result.result(Result.Message.OK); // unused
        default:
            log.error("Unhandled method {}", method);
            return Result.error(Result.Message.UNEXPECTED_METHOD);
        }
    }

    private static Map<String, String> extractParams(Map<String, String> map) {
        return map.keySet()
                .stream()
                .filter(k -> k.startsWith("params["))
                .sorted()
                .collect(Collectors.toMap(UnitpayController::cutPrefix, map::get, (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                }, LinkedHashMap::new));
    }

    private static String cutPrefix(String full) {
        return full.substring(7, full.length() - 1);
    }

}
