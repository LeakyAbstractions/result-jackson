/**{% if false %}*/

package example;

import static com.leakyabstractions.result.core.Results.failure;
import static com.leakyabstractions.result.core.Results.success;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.leakyabstractions.result.jackson.ResultModule;

@SuppressWarnings("java:S125")
@DisplayName("Example")
class Example_Test {

/** {% elsif include.test == "serialization_problem" %} Test serialization problem */
@Test
void testSerializationProblem() {
  // Given
  ApiResponse response = new ApiResponse("v1", success("Perfect"));
  // Then
  ObjectMapper objectMapper = new ObjectMapper();
  InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
      () -> objectMapper.writeValueAsString(response));
  assertTrue(error.getMessage().startsWith(
      "Java 8 optional type `java.util.Optional<java.lang.String>` not supported"));
} // End{% endif %}{% if false %}

@Test
void testExpectedSerializationErrorMessage() throws Exception {
  // Given
  ApiResponse response = new ApiResponse("v1", success("Perfect"));
  String expected;
  try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(
      "serialization_error.txt")))) {
    expected = br.lines().collect(Collectors.joining());
  }
  // Then
  ObjectMapper objectMapper = new ObjectMapper();
  InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
      () -> objectMapper.writeValueAsString(response));
  assertTrue(error.getMessage().replaceAll("\\n", "").startsWith(expected));
}

/** {% elsif include.test == "deserialization_problem" %} Test deserialization problem */
@Test
void testDeserializationProblem() {
  // Given
  String json = "{\"version\":\"v2\",\"result\":{\"success\":\"OK\"}}";
  // Then
  ObjectMapper objectMapper = new ObjectMapper();
  InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
      () -> objectMapper.readValue(json, ApiResponse.class));
  assertTrue(error.getMessage().startsWith(
      "Cannot construct instance of `com.leakyabstractions.result.api.Result`"));
} // End{% endif %}{% if false %}

@Test
void testExpectedDeserializationErrorMessage() throws Exception {
  // Given
  String json = "{\"version\":\"v2\",\"result\":{\"success\":\"OK\"}}";
  String expected;
  try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(
      "deserialization_error.txt")))) {
    expected = br.lines().collect(Collectors.joining());
  }
  // Then
  ObjectMapper objectMapper = new ObjectMapper();
  InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
      () -> objectMapper.readValue(json, ApiResponse.class));
  assertTrue(error.getMessage().replaceAll("\\n", "").startsWith(expected));
}

/** {% elsif include.test == "serialize_successful_result" %} Test serialization solution with a successful result */
@Test
void serializeSuccessfulResult() throws Exception {
  // Given
  ApiResponse response = new ApiResponse("v3", success("All good"));
  // When
  ObjectMapper objectMapper = new ObjectMapper();
  objectMapper.registerModule(new ResultModule());
  String json = objectMapper.writeValueAsString(response);
  // Then
  assertTrue(json.contains("v3"));
  assertTrue(json.contains("All good"));
} // End{% endif %}{% if false %}

/** {% elsif include.test == "serialize_failed_result" %} Test serialization problem with a failed result */
@Test
void serializeFailedResult() throws Exception {
  // Given
  ApiResponse response = new ApiResponse("v4", failure("Oops"));
  // When
  ObjectMapper objectMapper = new ObjectMapper();
  objectMapper.findAndRegisterModules();
  String json = objectMapper.writeValueAsString(response);
  // Then
  assertTrue(json.contains("v4"));
  assertTrue(json.contains("Oops"));
} // End{% endif %}{% if false %}

/** {% elsif include.test == "deserialize_successful_result" %} Test deserialization solution with a successful result */
@Test
void deserializeSuccessfulResult() throws Exception {
  // Given
  String json = "{\"version\":\"v5\",\"result\":{\"success\":\"Yay\"}}";
  // When
  ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
  ApiResponse response = objectMapper.readValue(json, ApiResponse.class);
  // Then
  assertEquals("v5", response.getVersion());
  assertEquals("Yay", response.getResult().orElse(null));
} // End{% endif %}{% if false %}

/** {% elsif include.test == "deserialize_failed_result" %} Test deserialization solution with a failed result */
@Test
void deserializeFailedResult() throws Exception {
  // Given
  String json = "{\"version\":\"v6\",\"result\":{\"failure\":\"Nay\"}}";
  // When
  ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
  ApiResponse response = objectMapper.readValue(json, ApiResponse.class);
  // Then
  assertEquals("v6", response.getVersion());
  assertEquals("Nay", response.getResult().getFailure().orElse(null));
} // End{% endif %}{% if false %}

}
// {% endif %}