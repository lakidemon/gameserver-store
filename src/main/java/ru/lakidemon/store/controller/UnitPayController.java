package ru.lakidemon.store.controller;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UnitPayController {

    @GetMapping("/endpoint")
    public void handle(@RequestParam("params") TestWrapper params) {
        System.out.println(params);
    }

    @ToString
    public static class TestWrapper {
        private Map<String, Object> customMap= new HashMap<>();

        public Map<String, Object> getCustomMap() {
            return customMap;
        }

        public void setCustomMap(Map<String, Object> customMap) {
            this.customMap = customMap;
        }
    }

    // запасной вариант
    private static Map<String, String> extractParams(Map<String, String> map) {
        return map.keySet()
                .stream()
                .filter(k -> k.startsWith("params["))
                .sorted()
                .collect(Collectors.toMap(UnitPayController::cutPrefix, map::get, (u,v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                }, LinkedHashMap::new));
    }

    private static String cutPrefix(String full) {
        return full.substring(7, full.length() - 1);
    }

}
