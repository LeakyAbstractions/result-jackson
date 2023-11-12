---
title: Jackson Datatype Module for Result
description: Result-Jackson provides a Jackson datatype module for Result objects
image: https://dev.leakyabstractions.com/result/result-magic-ball.png
---

# Result Jackson Module

This library provides a Jackson datatype module for [Results objects][RESULT].


## Introduction

When using [<tt>Result</tt> objects][RESULT_REPO] with [Jackson][JACKSON_REPO] we might run into some problems.
[This library][RESULT_JACKSON_REPO] solves them by making Jackson treat results as if they were ordinary objects.

Let's start by creating a class `ApiResponse` containing one ordinary and one <tt>Result</tt> field:

```java
class ApiResponse {

  @JsonProperty
  String version;

  @JsonProperty
  Result<Integer, String> result;

  // Constructors, getters and setters omitted
}
```


## Problem Overview

We will take a look at what happens when we try to serialize and deserialize <tt>ApiResponse</tt> objects with Jackson.

First, let's make sure we're using the latest versions of both libraries, [Jackson][JACKSON_LATEST] and
[Result][RESULT_LATEST].


### Serialization Problem

Now, let's instantiate an `ApiResponse`:

```java
ApiResponse response = new ApiResponse();
response.setVersion("v1");
response.setResult(success(1024));
```

And finally, let's try serializing it using an [object mapper][OBJECT_MAPPER]:

```java
ObjectMapper objectMapper = new ObjectMapper();
String json = objectMapper.writeValueAsString(response);
```

We'll see that the output of the <tt>Result</tt> field contains both `success` and `failure` but they look like nested
JSON objects with a boolean field `empty`:

```json
{
  "version": "v1",
  "result": {
    "failure": {
      "empty": true
    },
    "success": {
      "empty": false
    }
  }
}
```

> You may also see a `present` field along with `empty`, depending on the JDK version you are using.

Although this may look strange, it's actually what we should expect. In this case, `getSuccess()` and `getFailure()` are
public getters on the <tt>Result</tt> class, and both of them return an optional value. Now <tt>Optional</tt> class has
a public getter `isPresent()`, which means that, unless you have registered the
[Jackson modules that deal with JDK 8 datatypes][JACKSON_JAVA8_REPO], it will be serialized with a value of `true` or
`false`, depending on whether it is empty or not. This is Jackson's default serialization behavior.

```java
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
```

In fact, what we'd like to be serialized is the actual success value of the `result` field:

```json
{
  "version": "v1",
  "result": {
    "failure": null,
    "success": 1024
  }
}
```


### Deserialization Problem

Now, let's reverse our previous example, this time trying to deserialize a JSON object into an <tt>ApiResponse</tt>:

```java
String json = "{\"version\":\"v2\",\"result\":{\"success\":512}}";
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.readValue(json, ApiResponse.class);
```

We'll see that now we get an `InvalidDefinitionException`. Let's view the stack trace:

```
Cannot construct instance of `com.leakyabstractions.result.api.Result`
 (no Creators, like default constructor, exist):
 abstract types either need to be mapped to concrete types,
 have custom deserializer, or contain additional type information
 at [Source: (String)"{"version":"v2","result":{"success":512}}"; line: 1, column: 26]
 (through reference chain: ApiResponse["result"])
```

This behavior again makes sense. Essentially, Jackson doesn't have a clue how to create new <tt>Result</tt> objects,
because <tt>Result</tt> is just an interface, not a concrete type.

```java
@Test
void deserialization_problem() {
  // Given
  String json = "{\"version\":\"v2\",\"result\":{\"success\":512}}";
  // Then
  ObjectMapper objectMapper = new ObjectMapper();
  InvalidDefinitionException error = assertThrows(InvalidDefinitionException.class,
      () -> objectMapper.readValue(json, ApiResponse.class));
  assertTrue(error.getMessage().startsWith(
      "Cannot construct instance of `com.leakyabstractions.result.api.Result`"));
}
```


## Solution

What we want, is for Jackson to treat <tt>Result</tt> values as JSON objects that contain either a `success` or a
`failure` value. Fortunately, this problem has been solved for us. [This library][RESULT_JACKSON_REPO] provides a
Jackson module that deals with <tt>Result</tt> objects.

First, let's add the latest version as a Maven dependency:

- groupId: `com.leakyabstractions`
- artifactId: `result-jackson`
- version: `{{ site.current_version }}`

All we need to do now is register <tt>ResultModule</tt> with our object mapper:

```java
ObjectMapper objectMapper = new ObjectMapper();
objectMapper.registerModule(new ResultModule());
```

Alternatively, you can also auto-discover the module with:

```java
objectMapper.findAndRegisterModules();
```

Regardless of registration mechanism, after registration all functionality is available for all normal Jackson
operations.


### Serialization Solution

Now, let's try and serialize our `ApiResponse` object again:

```java
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
```

If we look at the serialized response, we'll see that this time the `result` field contains a null `failure` value and
a non-null `success` value:

```json
{
  "version": "v3",
  "result": {
    "failure": null,
    "success": 256
  }
}
```

Next, we can try serializing a failed result:

```java
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
```

And we can verify that the serialized response contains a non-null `failure` value and a null `success` value:

```json
{
  "version": "v4",
  "result": {
    "failure": "Hello",
    "success": null
  }
}
```


### Deserialization Solution

Now, let's repeat our tests for deserialization. If we reread our `ApiResponse` we'll see that we no longer get an
`InvalidDefinitionException`:

```java
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
```

Finally, let's repeat the test again, this time with a failed result. We'll see that yet again we don't get an
exception, and in fact, have a failed <tt>Result</tt>:

```java
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
```


## Conclusion

We've shown how to use [Result][RESULT] with [Jackson][JACKSON] without any problems by leveraging the
[Jackson datatype module for Result][RESULT_JACKSON_REPO], demonstrating how it enables Jackson to treat
<tt>Result</tt> objects as ordinary fields.

The implementation of these examples can be found [here][RESULT_JACKSON_EXAMPLE].


[JACKSON_JAVA8_REPO]:           https://github.com/FasterXML/jackson-modules-java8
[JACKSON_LATEST]:               https://search.maven.org/artifact/com.fasterxml.jackson.core/jackson-core/
[JACKSON_REPO]:                 https://github.com/FasterXML/jackson
[OBJECT_MAPPER]:                https://www.baeldung.com/jackson-object-mapper-tutorial
[RESULT]:                       https://dev.leakyabstractions.com/result/
[RESULT_JACKSON_EXAMPLE]:       https://github.com/LeakyAbstractions/result-jackson/blob/main/lib-jackson/src/test/java/com/leakyabstractions/result/jackson/Example_Test.java
[RESULT_JACKSON_REPO]:          https://github.com/LeakyAbstractions/result-jackson/
[RESULT_LATEST]:                https://search.maven.org/artifact/com.leakyabstractions/result/
[RESULT_REPO]:                  https://github.com/LeakyAbstractions/result/
