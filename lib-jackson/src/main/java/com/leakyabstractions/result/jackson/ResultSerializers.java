
package com.leakyabstractions.result.jackson;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.leakyabstractions.result.Result;

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
