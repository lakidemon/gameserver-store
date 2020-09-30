package ru.lakidemon.store.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import ru.lakidemon.store.unitpay.Result;

import java.io.IOException;
import java.util.Map;

public class ResultSerializer extends StdSerializer<Result> {
    protected ResultSerializer() {
        super(Result.class);
    }

    @Override
    public void serialize(Result value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField(value.isError() ? "error" : "result", Map.of("message", value.getMessage()));
        gen.writeEndObject();
    }
}
