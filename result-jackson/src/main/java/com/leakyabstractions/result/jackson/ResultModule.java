/*
 * Copyright 2025 Guillermo Calvo
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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Jackson datatype module for {@link com.leakyabstractions.result.api.Result Result} objects.
 *
 * @author <a href="https://guillermo.dev/">Guillermo Calvo</a>
 * @see com.leakyabstractions.result.jackson Jackson datatype module for Result
 */
public class ResultModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    private static final String GROUP_ID = "com.leakyabstractions";
    private static final String ARTIFACT_ID = "result-jackson";

    private static final int[] VERSION_NUMBERS = { 1, 0, 2, 1 };
    private static final String VERSION_SNAPSHOT = null;

    private static final int VERSION_GRADE = VERSION_NUMBERS[0];
    private static final int VERSION_MAJOR = VERSION_NUMBERS[1];
    private static final int VERSION_MINOR = VERSION_NUMBERS[2];
    private static final int VERSION_PATCH = VERSION_NUMBERS[3];

    public static final Version MODULE_VERSION = new Version(
            VERSION_GRADE * 1000 + VERSION_MAJOR,
            VERSION_MINOR,
            VERSION_PATCH,
            VERSION_SNAPSHOT,
            GROUP_ID,
            ARTIFACT_ID);

    /** Create a new instance of result jackson module. */
    public ResultModule() {
        super(MODULE_VERSION);
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new ResultSerializers());
        context.addDeserializers(new ResultDeserializers());
    }
}
