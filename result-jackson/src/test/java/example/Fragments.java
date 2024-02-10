/* {% if false %} */

package example;

import static com.leakyabstractions.result.core.Results.success;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leakyabstractions.result.jackson.ResultModule;

class Fragments {

  static void serialize() throws Exception {

/* {% elsif include.fragment == "instantiate" %} Create new response */
ApiResponse response = new ApiResponse();
response.setVersion("v1"); // Set version
response.setResult(success(1024)); // Set result{% endif %}{% if false %}

/* {% elsif include.fragment == "serialize" %} Serialize the response object */
ObjectMapper objectMapper = new ObjectMapper(); // Create new object mapper
String json = objectMapper.writeValueAsString(response); // Serialize as JSON{% endif %}{% if false %}

  }

  static void deserialize() throws Exception {

/* {% elsif include.fragment == "deserialize" %} Deserialize a JSON string */
String json = "{\"version\":\"v2\",\"result\":{\"success\":512}}";
ObjectMapper objectMapper = new ObjectMapper(); // Create new object mapper
objectMapper.readValue(json, ApiResponse.class); // Deserialize the response{% endif %}{% if false %}

  }

  static void register() throws Exception {

/* {% elsif include.fragment == "register_manually" %} Register ResultModule */
ObjectMapper objectMapper = new ObjectMapper(); // Create new object mapper
objectMapper.registerModule(new ResultModule()); // Register manually{% endif %}{% if false %}

/* {% elsif include.fragment == "register_automatically" %} Register ResultModule */
objectMapper.findAndRegisterModules(); // Register automatically{% endif %}{% if false %}

  }
}
// {% endif %}