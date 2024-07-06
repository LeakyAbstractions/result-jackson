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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.leakyabstractions.result.api.Result;

/**
 * Finds serializers for {@link Result} objects.
 *
 * @author <a href="https://guillermo.dev/">Guillermo Calvo</a>
 */
class ResultSerializers extends Serializers.Base {

    @Override
    public JsonSerializer<?> findSerializer(
            SerializationConfig config, JavaType type, BeanDescription description) {
        if (Result.class.isAssignableFrom(type.getRawClass())) {
            return new ResultSerializer(type);
        }
        return null;
    }
}
