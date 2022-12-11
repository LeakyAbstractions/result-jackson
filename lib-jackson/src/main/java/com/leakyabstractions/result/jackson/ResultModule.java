
package com.leakyabstractions.result.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

/** Jackson datatype module for {@link com.leakyabstractions.result.Result}. */
public class ResultModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    private static final String GROUP_ID = "com.leakyabstractions";
    private static final String ARTIFACT_ID = "result-jackson";

    private static final int[] VERSION_NUMBERS = { 0, 1, 0, 0 };
    private static final String VERSION_SNAPSHOT = null;

    private static final int VERSION_GRADE = VERSION_NUMBERS[0];
    private static final int VERSION_MAJOR = VERSION_NUMBERS[1];
    private static final int VERSION_MINOR = VERSION_NUMBERS[2];
    private static final int VERSION_PATCH = VERSION_NUMBERS[3];

    public static final Version VERSION = new Version(
            VERSION_GRADE * 1000 + VERSION_MAJOR,
            VERSION_MINOR,
            VERSION_PATCH,
            VERSION_SNAPSHOT,
            GROUP_ID,
            ARTIFACT_ID);

    /** Create a new instance of result jackson module. */
    public ResultModule() {
        super(VERSION);
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializers(new ResultSerializers());
        context.addDeserializers(new ResultDeserializers());
    }
}
