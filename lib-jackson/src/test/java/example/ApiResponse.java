/** Represents an API response{% if false %} */

package example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.leakyabstractions.result.api.Result;

/** {% endif %} */
public class ApiResponse {

  @JsonProperty
  String version;

  @JsonProperty
  Result<Integer, String> result;

  // Constructors, getters and setters omitted{% if false %}
  @JsonCreator
  public ApiResponse() {
    /* ... */
  }

  public ApiResponse(String version, Result<Integer, String> result) {
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
  } // {% endif %}
}