package ru.lakidemon.store.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.lakidemon.store.unitpay.RequestMethod;

@Component
public class RequestMethodConverter implements Converter<String, RequestMethod> {
    @Override
    public RequestMethod convert(String source) {
        return RequestMethod.valueOf(source.toUpperCase());
    }
}
