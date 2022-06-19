
package com.leakyabstractions.result.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.leakyabstractions.result.Result;

class ResultSerializer extends StdSerializer<Result<?, ?>> {

    private static final long serialVersionUID = 1L;

    ResultSerializer(JavaType type) {
        super(type);
    }

    @Override
    public void serialize(Result<?, ?> value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeObject(new ResultBuilder<>(value));
    }
}
