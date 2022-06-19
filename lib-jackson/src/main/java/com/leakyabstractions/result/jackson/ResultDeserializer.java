
package com.leakyabstractions.result.jackson;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.leakyabstractions.result.Result;

class ResultDeserializer extends StdDeserializer<Result<?, ?>> {

    private static final long serialVersionUID = 1L;

    ResultDeserializer(JavaType valueType) {
        super(valueType);
    }

    @Override
    public Result<?, ?> deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return Optional.ofNullable(parser.readValueAs(ResultBuilder.class))
                .map(x -> (Result<?, ?>) x.build())
                .orElse(null);
    }
}
