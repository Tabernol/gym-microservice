package com.krasnopolskyi.security.integration_tests.runner;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@SuiteDisplayName("Cucumber Security Tests")
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.krasnopolskyi.security.integration_tests.steps")
public class SecurityCucumberTest {
}
