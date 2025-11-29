/** Represents an API response{% if false %} */

package example;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.leakyabstractions.result.api.Result;

/** {% endif %} */
public final class ApiResponse {

  @JsonProperty
  String version;

  @JsonProperty
  Result<String, String> result;

  // Constructors, getters and setters omitted{% if false %}
  @JsonCreator
  public ApiResponse() {
    /* ... */
  }

  public ApiResponse(String version, Result<String, String> result) {
    this.setVersion(version);
    this.setResult(result);
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public Result<String, String> getResult() {
    return result;
  }

  public void setResult(Result<String, String> result) {
    this.result = result;
  } // {% endif %}
}
