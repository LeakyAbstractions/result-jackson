
package com.leakyabstractions.result.jackson;

import static com.leakyabstractions.result.Results.failure;
import static com.leakyabstractions.result.Results.success;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.leakyabstractions.result.Result;

/**
 * Implementation of the example described in the
 * <a href="https://github.com/LeakyAbstractions/result-jackson/blob/main/docs/index.md">documentation</a>.
 *
 * @author Guillermo Calvo
 */
@DisplayName("Example")
class Example_Test {

    static class ApiResponse {

        @JsonProperty
        String version;

        @JsonProperty
        Result<Integer, String> result;

        public ApiResponse() {
            /* ... */
        }

        public ApiResponse(String version, Result<Integer, String> result) {
            this();
            this.setVersion(version);
            this.setResult(result);
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Result<Integer, String> getResult() {
            return result;
        }

        public void setResult(Result<Integer, String> result) {
            this.result = result;
        }
    }

    @Test
    void serialization_problem() throws Exception {
        // Given
        ApiResponse response = new ApiResponse("v1", success(1024));
        // When
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(response);
        // Then
        assertTrue(json.contains("v1"));
        assertFalse(json.contains("1024"));
    }

    @Test
    void deserialization_problem() {
        // Given
        String json = "{\"version\":\"v2\",\"result\":{\"success\":512}}";
        // Then
        ObjectMapper objectMapper = new ObjectMapper();
        InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
                () -> objectMapper.readValue(json, ApiResponse.class));
        assertTrue(error.getMessage().startsWith(
                "Cannot construct instance of `com.leakyabstractions.result.Result`"));
    }

    @Test
    void serialization_solution_successful_result() throws Exception {
        // Given
        ApiResponse response = new ApiResponse("v3", success(256));
        // When
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ResultModule());
        String json = objectMapper.writeValueAsString(response);
        // Then
        assertTrue(json.contains("v3"));
        assertTrue(json.contains("256"));
    }

    @Test
    void serialization_solution_failed_result() throws Exception {
        // Given
        ApiResponse response = new ApiResponse("v4", failure("Hello"));
        // When
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        String json = objectMapper.writeValueAsString(response);
        // Then
        assertTrue(json.contains("v4"));
        assertTrue(json.contains("Hello"));
    }

    @Test
    void deserialization_solution_successful_result() throws Exception {
        // Given
        String json = "{\"version\":\"v5\",\"result\":{\"success\":128}}";
        // When
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        ApiResponse response = objectMapper.readValue(json, ApiResponse.class);
        // Then
        assertEquals("v5", response.getVersion());
        assertEquals(128, response.getResult().orElse(null));
    }

    @Test
    void deserialization_solution_failed_result() throws Exception {
        // Given
        String json = "{\"version\":\"v6\",\"result\":{\"failure\":\"Bye\"}}";
        // When
        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        ApiResponse response = objectMapper.readValue(json, ApiResponse.class);
        // Then
        assertEquals("v6", response.getVersion());
        assertEquals("Bye", response.getResult().getFailure().orElse(null));
    }
}
