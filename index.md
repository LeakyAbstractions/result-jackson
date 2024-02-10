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
{% include_relative result-jackson/src/test/java/example/ApiResponse.java %}
```


## Problem Overview

We will take a look at what happens when we try to serialize and deserialize <tt>ApiResponse</tt> objects with Jackson.

First, let's make sure we're using the latest versions of both libraries, [Jackson][JACKSON_LATEST] and
[Result][RESULT_LATEST].


### Serialization Problem

Now, let's instantiate an `ApiResponse` object:

```java
{% include_relative result-jackson/src/test/java/example/Fragments.java fragment="instantiate" %}
```

And finally, let's try serializing it using an [object mapper][OBJECT_MAPPER]:

```java
{% include_relative result-jackson/src/test/java/example/Fragments.java fragment="serialize" %}
```

We'll see that now we get an `InvalidDefinitionException`:

```
{% include_relative result-jackson/src/test/resources/serialization_error.txt %}
```

Although this may look strange, it's actually what we should expect. In this case, `getSuccess()` is a public getter on
the <tt>Result</tt> interface that returns an `Optional<String>` value, which is not supported by Jackson unless you
have registered the [modules that deal with JDK 8 datatypes][JACKSON_JAVA8_REPO].

```java
{% include_relative result-jackson/src/test/java/example/Example_Test.java test="serialization_problem" %}
```

This is Jackson's default serialization behavior. But we'd like to serialize the `result` field like this:

```json
{% include_relative result-jackson/src/test/resources/expected_serialized_result.json %}
```


### Deserialization Problem

Now, let's reverse our previous example, this time trying to deserialize a JSON object into an <tt>ApiResponse</tt>:

```java
{% include_relative result-jackson/src/test/java/example/Fragments.java fragment="deserialize" %}
```

We'll see that we get another `InvalidDefinitionException`. Let's view the stack trace:

```
{% include_relative result-jackson/src/test/resources/deserialization_error.txt %}
```

This behavior again makes sense. Essentially, Jackson doesn't have a clue how to create new <tt>Result</tt> objects,
because <tt>Result</tt> is just an interface, not a concrete type.

```java
{% include_relative result-jackson/src/test/java/example/Example_Test.java test="deserialization_problem" %}
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
{% include_relative result-jackson/src/test/java/example/Fragments.java fragment="register_manually" %}
```

Alternatively, you can also auto-discover the module with:

```java
{% include_relative result-jackson/src/test/java/example/Fragments.java fragment="register_automatically" %}
```

Regardless of the registration mechanism used, once the module is registered all functionality is available for all
normal Jackson operations.


### Serialization Solution

Now, let's try and serialize our `ApiResponse` object again:

```java
{% include_relative result-jackson/src/test/java/example/Example_Test.java test="serialization_solution_successful_result" %}
```

If we look at the serialized response, we'll see that this time the `result` field contains a null `failure` value and
a non-null `success` value:

```json
{% include_relative result-jackson/src/test/resources/serialization_solution_successful_result.json %}
```

Next, we can try serializing a failed result:

```java
{% include_relative result-jackson/src/test/java/example/Example_Test.java test="serialization_solution_failed_result" %}
```

And we can verify that the serialized response contains a non-null `failure` value and a null `success` value:

```json
{% include_relative result-jackson/src/test/resources/serialization_solution_failed_result.json %}
```


### Deserialization Solution

Now, let's repeat our tests for deserialization. If we read our `ApiResponse` again, we'll see that we no longer get an
`InvalidDefinitionException`:

```java
{% include_relative result-jackson/src/test/java/example/Example_Test.java test="deserialization_solution_successful_result" %}
```

Finally, let's repeat the test again, this time with a failed result. We'll see that yet again we don't get an
exception, and in fact, have a failed <tt>Result</tt>:

```java
{% include_relative result-jackson/src/test/java/example/Example_Test.java test="deserialization_solution_failed_result" %}
```


## Conclusion

We've shown how to use [Result][RESULT] with [Jackson][JACKSON_REPO] without any problems by leveraging the
[Jackson datatype module for Result][RESULT_JACKSON_REPO], demonstrating how it enables Jackson to treat
<tt>Result</tt> objects as ordinary fields.

The implementation of these examples can be found [here][RESULT_JACKSON_EXAMPLE].


[JACKSON_JAVA8_REPO]:           https://github.com/FasterXML/jackson-modules-java8
[JACKSON_LATEST]:               https://search.maven.org/artifact/com.fasterxml.jackson.core/jackson-core/
[JACKSON_REPO]:                 https://github.com/FasterXML/jackson
[OBJECT_MAPPER]:                https://www.baeldung.com/jackson-object-mapper-tutorial
[RESULT]:                       https://dev.leakyabstractions.com/result/
[RESULT_JACKSON_EXAMPLE]:       https://github.com/LeakyAbstractions/result-jackson/blob/main/result-jackson/src/test/java/example/Example_Test.java
[RESULT_JACKSON_REPO]:          https://github.com/LeakyAbstractions/result-jackson/
[RESULT_LATEST]:                https://search.maven.org/artifact/com.leakyabstractions/result/
[RESULT_REPO]:                  https://github.com/LeakyAbstractions/result/
