
package com.leakyabstractions.result.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.leakyabstractions.result.Result;

class ResultDeserializers extends Deserializers.Base {

    @Override
    public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
            BeanDescription description) {
        if (Result.class.isAssignableFrom(type.getRawClass())) {
            return new ResultDeserializer(config, type);
        }
        return null;
    }
}
