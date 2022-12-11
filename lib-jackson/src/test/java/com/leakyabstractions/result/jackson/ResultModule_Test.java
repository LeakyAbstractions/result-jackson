
package com.leakyabstractions.result.jackson;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.leakyabstractions.result.assertj.ResultAssertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leakyabstractions.result.Result;
import com.leakyabstractions.result.Results;

/**
 * Tests for {@link ResultModule}.
 *
 * @author Guillermo Calvo
 */
@DisplayName("ResultModule")
class ResultModule_Test {

    static final TypeReference<Result<String, Integer>> RESULT_TYPE = new TypeReference<Result<String, Integer>>() {
    };

    static class Foobar<T> {

        @JsonProperty
        T foo;

        @JsonProperty
        Boolean bar;

        Foobar() {}

        Foobar(T foo, boolean bar) {
            this.foo = foo;
            this.bar = bar;
        }
    }

    @Test
    void read_successful_result() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String json = "{\"success\":\"SUCCESS\"}";
        // When
        final Result<String, Integer> result = mapper.readValue(json, RESULT_TYPE);
        // Then
        assertThat(result).hasSuccess("SUCCESS");
    }

    @Test
    void read_successful_map_result() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String json = "{\"success\":{\"x\":\"SUCCESS\",\"y\":123}}";
        // When
        final Result<?, ?> result = mapper.readValue(json, Result.class);
        // Then
        assertThat(result)
                .hasSuccessSatisfying(
                        x -> {
                            assertThat(x).hasFieldOrPropertyWithValue("x", "SUCCESS");
                            assertThat(x).hasFieldOrPropertyWithValue("y", 123);
                        });
    }

    @Test
    void read_failed_result() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String json = "{\"failure\":404}";
        // When
        final Result<String, Integer> result = mapper.readValue(json, RESULT_TYPE);
        // Then
        assertThat(result).hasFailure(404);
    }

    @Test
    void read_null() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String json = "null";
        // When
        final Result<?, ?> result = mapper.readValue(json, Result.class);
        // Then
        assertThat(result).isNull();
    }

    @Test
    void read_empty_result_as_null() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String json = "{}";
        // When
        final Result<String, Integer> result = mapper.readValue(json, RESULT_TYPE);
        // Then
        assertThat(result).isNull();
    }

    @Test
    void read_malformed_result_as_failure() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String json = "{\"success\":123,\"failure\":321}";
        // When
        final Result<String, Integer> result = mapper.readValue(json, RESULT_TYPE);
        // Then
        assertThat(result).hasFailure(321);
    }

    @Test
    void write_successful_result() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules().setSerializationInclusion(NON_NULL);
        final Result<?, ?> result = Results.success("OK");
        // When
        final String json = mapper.writeValueAsString(result);
        // Then
        assertThat(json).isEqualTo("{\"success\":\"OK\"}");
    }

    @Test
    void write_failed_result() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules().setSerializationInclusion(NON_NULL);
        final Result<?, ?> result = Results.failure("FAILURE");
        // When
        final String json = mapper.writeValueAsString(result);
        // Then
        assertThat(json).isEqualTo("{\"failure\":\"FAILURE\"}");
    }

    @Test
    void read_complex_successful_result() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String json = "{\"success\":{\"foo\":[1,2,3],\"bar\":true}}";
        final TypeReference<Result<Foobar<List<Integer>>, ?>> RESULT_TYPE = new TypeReference<Result<Foobar<List<Integer>>, ?>>() {
        };
        // When
        final Result<Foobar<List<Integer>>, ?> result = mapper.readValue(json, RESULT_TYPE);
        // Then
        assertThat(result)
                .hasSuccessSatisfying(
                        x -> {
                            assertThat(x.foo).containsExactly(1, 2, 3);
                            assertThat(x.bar).isTrue();
                        });
    }

    @Test
    void read_complex_failed_result() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String json = "{\"failure\":{\"foo\":404}}";
        final TypeReference<Result<Integer, Foobar<Long>>> RESULT_TYPE = new TypeReference<Result<Integer, Foobar<Long>>>() {
        };
        // When
        final Result<Integer, Foobar<Long>> result = mapper.readValue(json, RESULT_TYPE);
        // Then
        assertThat(result)
                .hasFailureSatisfying(
                        x -> {
                            assertThat(x.foo).isEqualTo(404L);
                            assertThat(x.bar).isNull();
                        });
    }

    @Test
    void read_complex_null() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String json = "null";
        final TypeReference<Result<Foobar<Float>, String>> RESULT_TYPE = new TypeReference<Result<Foobar<Float>, String>>() {
        };
        // When
        final Result<?, ?> result = mapper.readValue(json, RESULT_TYPE);
        // Then
        assertThat(result).isNull();
    }

    @Test
    void read_complex_empty_result_as_null() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String json = "{}";
        final TypeReference<Result<Foobar<String>, BigDecimal>> RESULT_TYPE = new TypeReference<Result<Foobar<String>, BigDecimal>>() {
        };
        // When
        final Result<Foobar<String>, BigDecimal> result = mapper.readValue(json, RESULT_TYPE);
        // Then
        assertThat(result).isNull();
    }

    @Test
    void read_malformed_complex_result_as_failure() throws JsonProcessingException {
        // Given
        final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
        final String json = "{\"success\":{\"foo\":\"12.34\",\"bar\":true},\"failure\":321}";
        final TypeReference<Result<Foobar<BigDecimal>, Long>> RESULT_TYPE = new TypeReference<Result<Foobar<BigDecimal>, Long>>() {
        };
        // When
        final Result<Foobar<BigDecimal>, Long> result = mapper.readValue(json, RESULT_TYPE);
        // Then
        assertThat(result).hasFailure(321L);
    }
}
