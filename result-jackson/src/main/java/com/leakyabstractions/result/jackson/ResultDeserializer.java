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
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.leakyabstractions.result.api.Result;

/**
 * Deserializes {@link Result} objects.
 *
 * @author <a href="https://guillermo.dev/">Guillermo Calvo</a>
 */
class ResultDeserializer extends StdDeserializer<Result<?, ?>> {

    private static final long serialVersionUID = 1L;

    private final JavaType builderType;

    ResultDeserializer(DeserializationConfig config, JavaType valueType) {
        super(valueType);
        this.builderType = getBuilderType(config, valueType.getBindings().getTypeParameters());
    }

    @Override
    public Result<?, ?> deserialize(JsonParser parser, DeserializationContext context)
            throws IOException {
        return Optional.ofNullable(this.readBuilder(parser, context))
                .map(ResultBuilder::build)
                .orElse(null);
    }

    private ResultBuilder<?, ?> readBuilder(JsonParser parser, DeserializationContext context)
            throws IOException {
        if (this.builderType == null) return parser.readValueAs(ResultBuilder.class);
        return (ResultBuilder<?, ?>) context.findRootValueDeserializer(this.builderType).deserialize(parser, context);
    }

    private static JavaType getBuilderType(DeserializationConfig config, List<JavaType> typeParams) {
        if (typeParams.size() != 2) return null;
        return config
                .getTypeFactory()
                .constructSimpleType(ResultBuilder.class, typeParams.toArray(new JavaType[2]));
    }
}
