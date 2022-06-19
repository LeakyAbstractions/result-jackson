
package com.leakyabstractions.result.jackson;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.leakyabstractions.result.assertj.ResultAssertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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

    final static TypeReference<Result<String, Integer>> RESULT_TYPE = new TypeReference<Result<String, Integer>>() {
    };

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
}
