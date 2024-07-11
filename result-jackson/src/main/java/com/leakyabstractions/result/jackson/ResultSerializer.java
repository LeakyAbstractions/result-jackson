/*
 * Copyright 2024 Guillermo Calvo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leakyabstractions.result.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.leakyabstractions.result.api.Result;

/**
 * Serializes {@link Result} objects.
 *
 * @author <a href="https://guillermo.dev/">Guillermo Calvo</a>
 */
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
