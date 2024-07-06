/*
 * Copyright 2024 Guillermo Calvo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leakyabstractions.result.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.leakyabstractions.result.api.Result;
import com.leakyabstractions.result.core.Results;

/**
 * Builds {@link Result} objects.
 *
 * @author <a href="https://guillermo.dev/">Guillermo Calvo</a>
 * @param <S> the type of the success value
 * @param <F> the type of the failure value
 */
public class ResultBuilder<S, F> {

    @JsonProperty
    private S success;

    @JsonProperty
    private F failure;

    /** Creates a new instance of a result builder. */
    @JsonCreator
    public ResultBuilder() {
        /* ... */
    }

    /**
     * Creates a new instance of a result builder based on an existing result.
     *
     * @param result The result to base this builder on.
     */
    public ResultBuilder(Result<S, F> result) {
        result.ifSuccessOrElse(this::setSuccess, this::setFailure);
    }

    /**
     * Sets this builder's success value.
     *
     * @param success the success value
     */
    @JsonSetter
    public void setSuccess(S success) {
        this.success = success;
    }

    /**
     * Sets this builder's failure value.
     *
     * @param failure the failure value
     */
    @JsonSetter
    public void setFailure(F failure) {
        this.failure = failure;
    }

    /**
     * Builds a new result based on this builder.
     *
     * @return a new result object
     */
    public Result<S, F> build() {
        if (this.failure != null) {
            return Results.failure(this.failure);
        }
        if (this.success != null) {
            return Results.success(this.success);
        }
        return null;
    }
}
